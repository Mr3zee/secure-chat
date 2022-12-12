package com.example.secure.chat.web.routing.websocket

import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.web.controller.ChatController
import com.example.secure.chat.web.controller.MessageController
import com.example.secure.chat.web.controller.UserController
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RequestProcessorStrategy : KoinComponent {

    private val logger = KotlinLogging.logger { }

    private val chatController by inject<ChatController>()
    private val messageController by inject<MessageController>()
    private val userController by inject<UserController>()

    private fun chooseProcessor(
        request: ClientRequestDto<*, *>,
    ): suspend (WebSocketSessionContext) -> ServerResponseDto<*, *> {
        return when (request) {
            is ChatCreateRequestDto -> { context -> chatController.chatCreate(context, request) }
            is ChatListRequestDto -> { context -> chatController.chatList(context, request) }
            is ChatLeaveRequestDto -> { context -> chatController.chatLeave(context, request) }
            is ChatSubscribeRequestDto -> { context -> chatController.chatSubscribe(context, request) }
            is InviteAcceptRequestDto -> { context -> chatController.inviteAccept(context, request) }
            is InviteListRequestDto -> { context -> chatController.inviteList(context, request) }
            is InviteSendRequestDto -> { context -> chatController.inviteSend(context, request) }
            is GetUserPublicKeyRequestDto -> { _ -> userController.getUserPublicKey(request) }
            is MessageListRequestDto -> { context -> messageController.messageList(context, request) }
            is MessageSendRequestDto -> { context -> messageController.messageSend(context, request) }
            is LogoutRequestDto -> { _ -> LogoutResponseDto(request.requestId) }
        }
    }

    suspend fun process(
        context: WebSocketSessionContext,
        request: ClientRequestDto<*, *>,
    ) {
        val action = chooseProcessor(request)

        try {
            val response = action.invoke(context)
            context.sendSerialized(response)
            if (response is LogoutResponseDto) {
                context.close()
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to process request ${request::class.simpleName}" }
            try {
                context.closeExceptionally(e)
            } catch (ignored: Exception) {
            }
        }
    }
}
