package com.example.secure.chat.web.model.api

import com.example.auth.common.dto.request.CheckDecodedMessageRequestDto
import com.example.auth.common.dto.request.LoginRequestDto
import com.example.auth.common.dto.request.RegisterRequestDto
import com.example.auth.common.dto.request.SerializableAuthenticationRequestDto
import com.example.auth.common.dto.response.CheckDecodedMessageResponseDto
import com.example.auth.common.dto.response.LoginResponseDto
import com.example.auth.common.dto.response.RegisterResponseDto
import com.example.secure.chat.platform.backoff
import com.example.secure.chat.platform.client
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Invite
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.LoginContext
import com.example.secure.chat.web.utils.isDevEnv
import com.example.secure.chat.web.utils.toArrayBuffer
import com.example.secure.chat.web.utils.toBase64Bytes
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.isActive
import kotlinx.js.jso

object ChatApiImpl : ChatApi {
    private var session: DefaultClientWebSocketSession? = null

    private suspend fun <T> LoginContext.withSession(body: DefaultClientWebSocketSession.() -> T): T {
        val s = session ?: error("Session uninitialized")

        if (!s.isActive) {
            loginUser(this)
        }

        return s.body()
    }

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

    override suspend fun getLastMessage(chat: Chat.Global, key: PrivateCryptoKey, coder: Coder): Message? {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(
        chatName: String,
        initialMessage: Message,
        coder: Coder,
    ): Pair<Chat.Global, CryptoKeyPair> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChats(coder: Coder): List<Pair<Chat.Global, PublicCryptoKey>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatTimeline(chat: Chat.Global): List<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun leaveChat(chat: Chat.Global): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun inviteMember(chat: Chat.Global, username: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chat: Chat.Global, message: Message): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvite(chatName: String, invite: Invite): Result<Pair<Chat.Global, CryptoKeyPair>> {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeOnNewInvites(handler: (List<Invite>) -> Unit) {
        // todo
    }

    override suspend fun subscribeOnNewMessages(handler: (List<Pair<Long, Message>>) -> Unit) {
        // todo
    }
}