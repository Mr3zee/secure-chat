package com.example.secure.chat.web.controller.impl

import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.web.controller.ChatController
import com.example.secure.chat.web.controller.impl.converter.toDto
import com.example.secure.chat.web.controller.impl.converter.toModel
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ChatControllerImpl : ChatController, KoinComponent {

    private val chatService by inject<ChatService>()

    override suspend fun chatList(
        context: WebSocketSessionContext,
        rq: ChatListRequestDto,
    ): ChatListResponseDto {
        return ChatListResponseDto(
            rq.requestId,
            chatService.getAllChats(
                context.currentUser.id,
            ).map(::toDto)
        )
    }

    override suspend fun chatCreate(
        context: WebSocketSessionContext,
        rq: ChatCreateRequestDto,
    ): ChatCreateResponseDto {
        val (chat, message) = chatService.createChat(
            toModel(context.currentUser, rq.chat),
            toModel(rq.chat.publicKey),
            toModel(rq.startMessageText),
        )
        return ChatCreateResponseDto(
            rq.requestId,
            toDto(chat),
            toDto(message),
        )
    }

    override suspend fun chatLeave(
        context: WebSocketSessionContext,
        rq: ChatLeaveRequestDto,
    ): ChatLeaveResponseDto {
        return chatService.leaveChat(
            context.currentUser.id,
            rq.chatId,
        ).let { ChatLeaveResponseDto(rq.requestId) }
    }

    override suspend fun inviteList(
        context: WebSocketSessionContext,
        rq: InviteListRequestDto,
    ): InviteListResponseDto {
        return InviteListResponseDto(
            rq.requestId,
            chatService.getAllInvites(
                context.currentUser.id,
            ).map(::toDto),
        )
    }

    override suspend fun inviteSend(
        context: WebSocketSessionContext,
        rq: InviteSendRequestDto,
    ): InviteSendResponseDto {
        return chatService.sendInvite(
            toModel(rq.invite),
        ).let { InviteSendResponseDto(rq.requestId) }
    }

    override suspend fun inviteAccept(
        context: WebSocketSessionContext,
        rq: InviteAcceptRequestDto,
    ): InviteAcceptResponseDto {
        return InviteAcceptResponseDto(
            rq.requestId,
            chatService.acceptInvite(
                toModel(context.currentUser, rq.invite, rq.chatName),
            ).let(::toDto),
        )
    }
}
