package com.example.secure.chat.web.routing.websocket

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.CheckDecodedMessageResponseDto
import com.example.auth.common.dto.response.LoginResponseDto
import com.example.auth.common.dto.response.RegisterResponseDto
import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.web.controller.UserController
import com.example.secure.chat.web.controller.impl.converter.toDto
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import org.koin.ktor.ext.inject
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

fun Routing.websocketRouting() {
    webSocket("/websocket") {
        val context = WebSocketSessionContext(this, authenticate(this))
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
            val word = UUID.randomUUID().toString().encodeToByteArray()
            val loginResponse = LoginResponseDto(
                RawBytesDto(encode(user, word))
            )
            session.sendSerialized(loginResponse)
            val request = session.receiveDeserialized<CheckDecodedMessageRequestDto>()
            if (!word.contentEquals(request.decodedMessage.content)) {
                throw IllegalArgumentException("Original end decoded messages are not matching")
            }
            val checkResponse = CheckDecodedMessageResponseDto(toDto(user.publicKey))
            session.sendSerialized(checkResponse)
            user
        }

        is RegisterRequestDto -> {
            val createRq = UserCreateRq(
                action.userLogin,
                ByteArrayWrapper(action.publicKey.content),
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
    val keySpec = X509EncodedKeySpec(user.publicKey.byteArray)
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
        context.closeExceptionally(e)
    }
}
