package com.example.secure.chat.web.model.api

import com.example.auth.common.dto.model.chat.ChatDraftDto
import com.example.auth.common.dto.model.message.MessageDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.backoff
import com.example.secure.chat.platform.client
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.LoginContext
import com.example.secure.chat.web.model.creds.unsureKey
import com.example.secure.chat.web.utils.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.js.jso

object ChatApiImpl : ChatApi {
    private var session: DefaultClientWebSocketSession? = null

    private val requestId = atomic(0L)

    private suspend fun createSession(): DefaultClientWebSocketSession {
        return client.webSocketSession("/websocket") {
            url {
                if (isDevEnv()) {
                    protocol = URLProtocol.WS
                    port = 8080
                } else {
                    protocol = URLProtocol.WSS // secure
                    port = 80
                }
            }
        }
    }

    private suspend fun <T> LoginContext.withSession(body: suspend DefaultClientWebSocketSession.() -> T): T {
        val s = session ?: error("Session uninitialized")

        if (!s.isActive) {
            loginUser(this)

            return withSession(body)
        }

        s.collectServerResponses()

        return s.body()
    }

    private suspend fun <T> LoginContext.backoffWithSession(body: suspend DefaultClientWebSocketSession.() -> T): T? {
        return backoff {
            withSession(body)
        }
    }

    private val backlog = mutableListOf<SerializableServerResponseDto>()

    private val channel = Channel<SerializableServerResponseDto>(Channel.UNLIMITED)

    private fun DefaultClientWebSocketSession.collectServerResponses() {
        launch(Ui) {
            while (isActive) {
                val response = receiveDeserialized<SerializableServerResponseDto>()
                channel.send(response)
            }
        }
    }

    private suspend inline fun <REQUEST_TYPE, reified RESPONSE_TYPE> LoginContext.receiveExact(id: Long): RESPONSE_TYPE
            where RESPONSE_TYPE : ServerResponseDto<REQUEST_TYPE, RESPONSE_TYPE>,
                  REQUEST_TYPE : ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE> {
        val response = backlog.filterIsInstance<RESPONSE_TYPE>().singleOrNull { it.requestId == id }

        response?.let {
            backlog.remove(it)

            return it
        }

        val res = withSession {
            if (response == null) {
                while (isActive) {
                    val new = channel.receive()
                    if (new is RESPONSE_TYPE && new.requestId == id) {
                        return@withSession new
                    }
                    backlog.add(new)
                }
            }

            null
        } ?: error("Session closed.")

        return res
    }

    private suspend inline fun <REQUEST_TYPE, reified RESPONSE_TYPE> LoginContext.requestAndReceive(
        requestDto: (id: Long) -> ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE>,
    ): RESPONSE_TYPE? where RESPONSE_TYPE : ServerResponseDto<REQUEST_TYPE, RESPONSE_TYPE>,
                            REQUEST_TYPE : ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE> {
        val id = requestId.getAndIncrement()
        val request = requestDto(id)

        backoffWithSession {
            sendSerialized<SerializableClientRequestDto>(request)
        }

        return backoff {
            receiveExact<REQUEST_TYPE, RESPONSE_TYPE>(id)
        }
    }

    private fun <T> failBackoff() = fail<T>("Operation with backoff failed.")

    private fun <T> fail(message: String) = Result.failure<T>(IllegalStateException(message))

    override suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean {
        val localSession = backoff { createSession() } ?: return false

        val bytesKey = crypto.exportPublicRSAKey(keyPair.publicKey).toBase64Bytes()

        backoff {
            localSession.sendSerialized<SerializableAuthenticationRequestDto>(RegisterRequestDto(username, bytesKey))
        } ?: return false

        backoff {
            localSession.receiveDeserialized<RegisterResponseDto>()
        } ?: return false

        session = localSession

        return true
    }

    override suspend fun loginUser(context: LoginContext): Result<CryptoKeyPair> = with(context) {
        val localSession = backoff { createSession() } ?: return failBackoff()

        backoff {
            localSession.sendSerialized<SerializableAuthenticationRequestDto>(LoginRequestDto(username))
        } ?: return failBackoff()

        val response = backoff {
            localSession.receiveDeserialized<LoginResponseDto>()
        } ?: return failBackoff()

        val encoded = response.encodedMessage.toArrayBuffer()

        val decoded = coder.safeDecryptRSA(privateCryptoKey, encoded) ?: return fail("Failed to decode")

        backoff {
            localSession.sendSerialized<SerializableAuthenticationRequestDto>(
                CheckDecodedMessageRequestDto(
                    username,
                    decoded.toBase64Bytes()
                )
            )
        } ?: return failBackoff()

        val publicKeyBytes = backoff {
            localSession.receiveDeserialized<CheckDecodedMessageResponseDto>()
        }?.publicKey ?: return failBackoff()

        val publicCryptoKey = coder.safeImportRSAPublicKey(publicKeyBytes.toArrayBuffer())
            ?: return fail("Failed to import public key")

        session = localSession

        return Result.success(jso {
            privateKey = privateCryptoKey
            publicKey = publicCryptoKey
        })
    }

    override suspend fun getLastMessage(
        context: LoginContext,
        chat: Chat.Global,
        key: PrivateCryptoKey,
    ): Result<Message?> {
        val response = context.requestAndReceive {
            MessageListRequestDto(it, chat.id, null, 1)
        } ?: return failBackoff()

        val message = response.messages.firstOrNull()?.toMessage(context)

        return Result.success(message)
    }

    override suspend fun createChat(
        context: LoginContext,
        chatName: String,
        initialMessage: Message,
    ): Result<Pair<Chat.Global, CryptoKeyPair>> {
        val encodedName = context.safeEncryptRSA(context.publicCryptoKey.unsureKey(), chatName)?.toBase64Bytes()
            ?: error("Unable to encrypt chatName")

        val keyPair = context.genRsaKeyPair()
        val encodedPublicKey = context.exportPublicRSAKey(keyPair.publicKey).toBase64Bytes()

        val draft = ChatDraftDto(encodedName, encodedPublicKey)
        val startMessageText = context.safeEncryptRSA(keyPair.publicKey, initialMessage.text)?.toBase64Bytes()
            ?: error("Unable to encrypt message")

        val chatDto = context.requestAndReceive { ChatCreateRequestDto(it, draft, startMessageText) }
            ?: return failBackoff()


        val newChat = Chat.Global(chatDto.chat.id, chatName)

        newChat.isLocked.value = false
        newChat.lastMessage.value = initialMessage.copy(
            timestamp = chatDto.startMessage.timestamp.zoned(),
            id = chatDto.startMessage.id,
            initialStatus = MessageStatus.Verified
        )

        return Result.success(newChat to keyPair)
    }

    override suspend fun getAllChats(context: LoginContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>> {
        val chats = context.requestAndReceive { ChatListRequestDto(it) }?.chats ?: return failBackoff()

        return chats.map {
            val pk = context.importRSAPublicKey(it.publicKey.toArrayBuffer())

            val name = context.safeDecryptRSA(context.privateCryptoKey, it.name.toArrayBuffer())?.asString()
                ?: error("Failed to decrypt chat name")

            val chat = Chat.Global(it.id, name)

            chat to pk
        }.let { Result.success(it) }
    }

    override suspend fun getChatTimeline(context: LoginContext, chat: Chat.Global): Result<List<Message>> {
        val response = context.requestAndReceive {
            MessageListRequestDto(it, chat.id, null, 1000)
        } ?: return failBackoff()

        return response.messages.map { it.toMessage(context) }.success()
    }

    override suspend fun leaveChat(context: LoginContext, chat: Chat.Global): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun inviteMember(context: LoginContext, chat: Chat.Global, username: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(context: LoginContext, chat: Chat.Global, message: Message): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvite(
        context: LoginContext,
        chatName: String,
        invite: Invite,
    ): Result<Pair<Chat.Global, CryptoKeyPair>> {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeOnNewInvites(context: LoginContext, handler: (List<Invite>) -> Unit) {
        // todo
    }

    override suspend fun subscribeOnNewMessages(context: LoginContext, handler: (List<Pair<Long, Message>>) -> Unit) {
        // todo
    }
}

private suspend fun MessageDto.toMessage(context: LoginContext) = Message(
    author = Author.User(userLogin),
    text = context.safeDecryptRSA(
        context.chatKeys[chatId]?.privateKey ?: error("Expected private key for chat $chatId"),
        encodedText.toArrayBuffer()
    )?.asString() ?: error("Failed to decrypt message"),
    id = id,
    timestamp = timestamp.zoned(),
    isSecret = false,
    initialStatus = MessageStatus.Verified,
)
