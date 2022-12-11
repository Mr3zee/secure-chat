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

    override fun chatList(
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

    override fun chatCreate(
        context: WebSocketSessionContext,
        rq: ChatCreateRequestDto,
    ): ChatCreateResponseDto {
        val (chat, message) = chatService.createChat(
            toModel(context.currentUser, rq.chat),
            toModel(context.currentUser, rq.startMessage),
        )
        return ChatCreateResponseDto(
            rq.requestId,
            toDto(chat),
            toDto(message),
        )
    }

    override fun inviteList(
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

    override fun inviteSend(
        context: WebSocketSessionContext,
        rq: InviteSendRequestDto,
    ): InviteSendResponseDto {
        return chatService.sendInvite(
            toModel(rq.invite),
        ).let { InviteSendResponseDto(rq.requestId) }
    }

    override fun inviteAccept(
        context: WebSocketSessionContext,
        rq: InviteAcceptRequestDto,
    ): InviteAcceptResponseDto {
        return InviteAcceptResponseDto(
            rq.requestId,
            chatService.acceptInvite(
                toModel(context.currentUser, rq.invite),
            ).let(::toDto),
        )
    }
}
