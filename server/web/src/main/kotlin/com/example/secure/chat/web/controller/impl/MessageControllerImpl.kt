package com.example.secure.chat.web.controller.impl

import com.example.auth.common.dto.request.MessageListRequestDto
import com.example.auth.common.dto.request.MessageSendRequestDto
import com.example.auth.common.dto.response.MessageListResponseDto
import com.example.auth.common.dto.response.MessageSendResponseDto
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.core.service.MessageService
import com.example.secure.chat.web.controller.MessageController
import com.example.secure.chat.web.controller.impl.converter.toDto
import com.example.secure.chat.web.controller.impl.converter.toModel
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object MessageControllerImpl : MessageController, KoinComponent {

    private val messageService by inject<MessageService>()
    private val chatService by inject<ChatService>()

    override suspend fun messageList(
        context: WebSocketSessionContext,
        rq: MessageListRequestDto,
    ): MessageListResponseDto {
        val pk = chatService.getChatById(rq.chatId).publicKey

        val (messages, hasMore) = messageService.getMessages(
            rq.chatId,
            rq.lastMessageId,
            rq.limit,
        )
        return MessageListResponseDto(
            rq.requestId,
            messages.map(::toDto),
            toDto(pk),
            hasMore,
        )
    }

    override suspend fun messageSend(
        context: WebSocketSessionContext,
        rq: MessageSendRequestDto,
    ): MessageSendResponseDto {
        return MessageSendResponseDto(
            rq.requestId,
            messageService.sendMessage(
                toModel(context.currentUser, rq.message),
            ).let(::toDto),
        )
    }
}
