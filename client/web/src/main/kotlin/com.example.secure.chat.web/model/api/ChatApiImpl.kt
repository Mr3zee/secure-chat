package com.example.secure.chat.web.model.api

import com.example.auth.common.dto.model.chat.ChatDraftDto
import com.example.auth.common.dto.model.invite.InviteDraftDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.model.message.MessageDraftDto
import com.example.auth.common.dto.model.message.MessageDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.backoff
import com.example.secure.chat.platform.client
import com.example.secure.chat.platform.safeLaunch
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.ApiContext
import com.example.secure.chat.web.model.creds.unsureKey
import com.example.secure.chat.web.utils.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.js.jso
import kotlin.reflect.KClass

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

    private suspend fun <T> ApiContext.withSession(body: suspend DefaultClientWebSocketSession.() -> T): T {
        val s = session ?: error("Session uninitialized")

        if (!s.isActive) {
            loginUser(this)

            return withSession(body)
        }

        return s.body()
    }

    private suspend fun <T> ApiContext.backoffWithSession(body: suspend DefaultClientWebSocketSession.() -> T): T? {
        return backoff {
            withSession(body)
        }
    }

    private val backlog = mutableListOf<SerializableServerResponseDto>()

    private var channel: Channel<SerializableServerResponseDto>? = null

    private val serverChannel: Channel<SerializableServerResponseDto> get() = channel ?: error("Channel closed")

    private val serverChannelSubscribers = mutableListOf<ServerSubscription<*>>()

    private data class ServerSubscription<E : ServerEventDto>(
        val channel: Channel<ServerEventDto>,
        val clazz: KClass<E>,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private inline fun <reified E : ServerEventDto> subscribeOnServerEvent(crossinline handler: suspend (E) -> Unit) {
        val channel = Channel<ServerEventDto>(Channel.UNLIMITED)
        safeLaunch(Ui) {
            while (!channel.isClosedForReceive) {
                val event = channel.receive()
                handler(event as E)
            }
        }
        val sub = ServerSubscription(channel, E::class)

        serverChannelSubscribers.add(sub)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun DefaultClientWebSocketSession.collectServerResponses() {
        safeLaunch(Ui) {
            outer@ while (isActive && !serverChannel.isClosedForSend) {
                val response = receiveDeserialized<SerializableServerResponseDto>()

                for (it in serverChannelSubscribers) {
                    if (it.clazz.isInstance(response)) {
                        it.channel.send(response as ServerEventDto)
                        continue@outer
                    }
                }

                serverChannel.send(response)
            }
        }
    }

    private fun initSession(session: DefaultClientWebSocketSession) {
        this.session = session
        this.channel = Channel(Channel.UNLIMITED)

        this.session?.collectServerResponses()
    }

    private suspend inline fun <REQUEST_TYPE, reified RESPONSE_TYPE> ApiContext.receiveExact(id: Long): RESPONSE_TYPE
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
                    val new = serverChannel.receive()
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

    private suspend inline fun <REQUEST_TYPE, reified RESPONSE_TYPE> ApiContext.requestAndReceive(
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

        initSession(localSession)

        return true
    }

    override suspend fun loginUser(context: ApiContext): Result<CryptoKeyPair> = with(context) {
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

        initSession(localSession)

        return Result.success(jso {
            privateKey = privateCryptoKey
            publicKey = publicCryptoKey
        })
    }

    override suspend fun logoutUser(): Boolean {
        session?.close()
        session = null
        backlog.clear()
        channel?.close()
        serverChannelSubscribers.forEach { it.channel.close() }
        serverChannelSubscribers.clear()
        return true
    }

    override suspend fun getLastMessage(
        context: ApiContext,
        chat: Chat.Global,
        key: PrivateCryptoKey,
    ): Result<Pair<Message?, PublicCryptoKey>> {
        val response = context.requestAndReceive {
            MessageListRequestDto(it, chat.id, null, 1)
        } ?: return failBackoff()

        val message = response.messages.firstOrNull()?.toMessage(context, key)

        val pk = context.importRSAPublicKey(response.publicKey.toArrayBuffer()).unsureKey()

        return Result.success(message to pk)
    }

    override suspend fun createChat(
        context: ApiContext,
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

    override suspend fun getAllChats(context: ApiContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>> {
        val chats = context.requestAndReceive { ChatListRequestDto(it) }?.chats ?: return failBackoff()

        return chats.map {
            val pk = context.importRSAPublicKey(it.publicKey.toArrayBuffer())

            val name = context.safeDecryptRSA(context.privateCryptoKey, it.name.toArrayBuffer())?.asString()
                ?: error("Failed to decrypt chat name")

            val chat = Chat.Global(it.id, name)

            chat to pk
        }.let { Result.success(it) }
    }

    override suspend fun getChatTimeline(context: ApiContext, chat: Chat.Global): Result<List<Message>> {
        val response = context.requestAndReceive {
            MessageListRequestDto(it, chat.id, null, 1000)
        } ?: return failBackoff()

        val privateCryptoKey = context.chatKeys[chat.id]?.privateKey
            ?: error("Expected private key for chat ${chat.id}")

        return response.messages.map { it.toMessage(context, privateCryptoKey) }.success()
    }

    override suspend fun leaveChat(context: ApiContext, chat: Chat.Global): Boolean {
        context.requestAndReceive { ChatLeaveRequestDto(it, chat.id) }
            ?: return false

        return true
    }

    override suspend fun sendMessage(context: ApiContext, chat: Chat.Global, message: Message): Message? {
        val (pub, priv) = context.chatKeys[chat.id] ?: error("Expected chat ${chat.id} public key")

        val encodedText = context.safeEncryptRSA(pub, message.text)?.toBase64Bytes()
            ?: error("Failed to encrypt message")

        val draft = MessageDraftDto(chat.id, encodedText)

        return context.requestAndReceive { MessageSendRequestDto(it, draft) }?.message?.toMessage(context, priv)
    }

    override suspend fun listInvites(context: ApiContext): Result<List<Invite>> {
        val result = context.requestAndReceive { InviteListRequestDto(it) }
            ?: return failBackoff()

        return result.invites.map { Invite(it.chatId, it.encodedKey) }.success()
    }

    override suspend fun inviteMember(context: ApiContext, chat: Chat.Global, username: String): Boolean {
        val chatSk = context.chatKeys[chat.id]?.privateKey ?: error("Expected chat ${chat.id} private key")

        val userPkEncoded = context.requestAndReceive { GetUserPublicKeyRequestDto(it, username) }?.publicKey
            ?: return false

        val userPk = context.importRSAPublicKey(userPkEncoded.toArrayBuffer())

        val encodedSk = context.safeEncryptRSA(userPk, context.exportPrivateRSAKey(chatSk))?.toBase64Bytes()
            ?: error("Failed to export private key")

        val draft = InviteDraftDto(username, chat.id, encodedSk)

        context.requestAndReceive { InviteSendRequestDto(it, username, draft) }
            ?: return false

        return true
    }

    override suspend fun acceptInvite(
        context: ApiContext,
        chatName: String,
        invite: Invite,
    ): Result<Pair<Chat.Global, CryptoKeyPair>> {
        val inviteDto = InviteDto(invite.chatId, invite.encodedKey)

        val privateKeyDer = context.safeDecryptRSA(context.privateCryptoKey, invite.encodedKey.toArrayBuffer())
            ?: error("Failed to decode chat's private key")
        val privateKey = context.importRSAPrivateKey(privateKeyDer)

        val encodedName = context.safeEncryptRSA(context.publicCryptoKey.unsureKey(), chatName)?.toBase64Bytes()
            ?: error("Failed to encode chat name")

        val response = context.requestAndReceive { InviteAcceptRequestDto(it, inviteDto, encodedName) }
            ?: return failBackoff()

        val publicKey = context.importRSAPublicKey(response.acceptedChat.publicKey.toArrayBuffer())

        val chat = Chat.Global(response.acceptedChat.id, chatName).apply {
            this.isLocked.value = false
        }

        return (chat to jso<CryptoKeyPair> {
            this.privateKey = privateKey
            this.publicKey = publicKey
        }).success()
    }

    override fun subscribeOnNewInvites(context: ApiContext, handler: (List<Invite>) -> Unit) {
        subscribeOnServerEvent<NewInvitesEventDto> {
            val invites = it.invites.map { invite -> Invite(invite.chatId, invite.encodedKey) }
            handler(invites)
        }
    }

    override fun subscribeOnNewMessages(context: ApiContext, handler: (List<Pair<Long, Message>>) -> Unit) {
        subscribeOnServerEvent<NewMessagesEventDto> {
            val messages = it.messages.map { message ->
                val privateCryptoKey = context.chatKeys[message.chatId]?.privateKey
                    ?: error("Expected private key for chat ${message.chatId}")

                message.chatId to message.toMessage(context, privateCryptoKey)
            }
            handler(messages)
        }
    }

    override suspend fun addChatSubscription(context: ApiContext, chat: Chat.Global) {
        context.requestAndReceive { ChatSubscribeRequestDto(it, chat.id) }
    }
}

private suspend fun MessageDto.toMessage(context: ApiContext, privateCryptoKey: PrivateCryptoKey) = Message(
    author = Author.User(userLogin),
    text = context.safeDecryptRSA(
        privateKey = privateCryptoKey,
        data = encodedText.toArrayBuffer()
    )?.asString() ?: error("Failed to decrypt message"),
    id = id,
    timestamp = timestamp.zoned(),
    isSecret = false,
    initialStatus = MessageStatus.Verified,
)
