package com.example.secure.chat.web.model.api

import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.backoff
import com.example.secure.chat.platform.client
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Invite
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.LoginContext
import com.example.secure.chat.web.utils.asString
import com.example.secure.chat.web.utils.isDevEnv
import com.example.secure.chat.web.utils.toArrayBuffer
import com.example.secure.chat.web.utils.toBase64Bytes
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

    private suspend inline fun <REQUEST_TYPE, reified RESPONSE_TYPE> LoginContext.sendAndReceive(
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
    ): Result<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(
        context: LoginContext,
        chatName: String,
        initialMessage: Message,
    ): Pair<Chat.Global, CryptoKeyPair> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChats(context: LoginContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>> {
        val chats = context.sendAndReceive { ChatListRequestDto(it) }?.chats ?: return failBackoff()

        return chats.map {
            val pk = context.coder.importRSAPublicKey(it.publicKey.toArrayBuffer())

            val name = context.coder.safeDecryptRSA(context.privateCryptoKey, it.name.toArrayBuffer())?.asString()
                ?: error("Failed to decrypt chat name")

            val chat = Chat.Global(it.id, name)

            chat to pk
        }.let { Result.success(it) }
    }

    override suspend fun getChatTimeline(context: LoginContext, chat: Chat.Global): Result<List<Message>> {
        TODO("Not yet implemented")
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
