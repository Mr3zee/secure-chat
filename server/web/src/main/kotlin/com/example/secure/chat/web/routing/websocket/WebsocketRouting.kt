package com.example.secure.chat.web.routing.websocket

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.CheckDecodedMessageResponseDto
import com.example.auth.common.dto.response.LoginResponseDto
import com.example.auth.common.dto.response.RegisterResponseDto
import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.web.controller.UserController
import com.example.secure.chat.web.controller.impl.converter.toDto
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

private val sessionId = AtomicLong()

private val logger = KotlinLogging.logger { }

fun Routing.websocketRouting() {
    webSocket("/websocket") {
        val context = try {
            WebSocketSessionContext(
                sessionId.incrementAndGet(),
                authenticate(this),
                this,
            )
        } catch (e: Exception) {
            closeExceptionally(e)
            logger.error(e) { "Failed to authenticate user" }
            return@webSocket
        }
        handleSession(context)
    }
}

suspend fun Routing.authenticate(
    session: WebSocketServerSession,
): User {
    val userController by inject<UserController>()
    return when (val action = session.receiveDeserialized<SerializableAuthenticationRequestDto>()) {

        is LoginRequestDto -> {
            val user = userController.getUser(action.userLogin)
                ?: throw NoSuchElementException("Not found user with login = ${action.userLogin}")
            val word = UUID.randomUUID().toString().toByteArray(Charsets.UTF_8)
            val content = encode(user, word)
            val loginResponse = LoginResponseDto(
                Base64BytesDto(Base64.getEncoder().encodeToString(content))
            )
            session.sendSerialized(loginResponse)
            val request = session.receiveDeserialized<CheckDecodedMessageRequestDto>()
            if (!word.contentEquals(Base64.getDecoder().decode(request.decodedMessage.content))) {
                throw IllegalArgumentException("Original end decoded messages are not matching")
            }
            val checkResponse = CheckDecodedMessageResponseDto(toDto(user.publicKey))
            session.sendSerialized(checkResponse)
            user
        }

        is RegisterRequestDto -> {
            val createRq = UserCreateRq(
                action.userLogin,
                Base64Bytes(action.publicKey.content),
            )
            val user = userController.register(createRq)
            val response = RegisterResponseDto(toDto(user.publicKey))
            session.sendSerialized(response)
            user
        }

        else -> throw IllegalArgumentException("Unexpected action ${action::class.simpleName}")
    }
}

fun encode(user: User, message: ByteArray): ByteArray {
    val rawBytes = Base64.getDecoder().decode(user.publicKey.content)
    val keySpec = X509EncodedKeySpec(rawBytes)
    val key: PublicKey = KeyFactory.getInstance("RSA")
        .generatePublic(keySpec)
    val params = OAEPParameterSpec(
        "SHA-512",
        "MGF1",
        MGF1ParameterSpec("SHA-512"),
        PSource.PSpecified.DEFAULT,
    )
    val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding").apply {
        init(Cipher.ENCRYPT_MODE, key, params)
    }
    return cipher.doFinal(message)
}

suspend fun handleSession(context: WebSocketSessionContext) {
    try {
        do {
            when (val action = context.receiveDeserialized<SerializableClientRequestDto>()) {
                is ClientRequestDto<*, *> -> RequestProcessorStrategy.process(context, action)
            }
        } while (context.isActive)
    } catch (e: Exception) {
        logger.error(e) { "Failed to handle session" }
        context.closeExceptionally(e)
    }
}
