package com.example.secure.chat.web.routing.websocket

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.LoginResponseDto
import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.web.controller.UserController
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import org.koin.ktor.ext.inject
import java.util.*

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
    return when (val action = session.receiveDeserialized<AuthenticationRequestDto<*, *>>()) {

        is LoginRequestDto -> {
            val user = userController.getUser(action.userLogin)
            val word = UUID.randomUUID().toString().encodeToByteArray()
            val response = LoginResponseDto(
                RawBytesDto(encode(user, word))
            )
            session.sendSerialized(response)
            val request = session.receiveDeserialized<CheckDecodedMessageRequestDto>()
            if (!word.contentEquals(request.decodedMessage.content)) {
                throw IllegalArgumentException("Original end decoded messages are not matching")
            }
            user
        }

        is RegisterRequestDto -> {
            val createRq = UserCreateRq(
                action.userLogin,
                ByteArrayWrapper(action.publicKey.content),
            )
            userController.register(createRq)
        }

        else -> throw IllegalArgumentException("Unexpected action ${action::class.simpleName}")
    }
}

fun encode(user: User, message: ByteArray): ByteArray {
    TODO()
}

suspend fun handleSession(context: WebSocketSessionContext) {
    try {
        do {
            val action = context.receiveDeserialized<ClientRequestDto<*, *>>()
            RequestProcessorStrategy.process(context, action)
        } while (context.isActive)
    } catch (e: Exception) {
        context.closeExceptionally(e)
    }
}
