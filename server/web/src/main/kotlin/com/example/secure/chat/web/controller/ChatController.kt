package com.example.secure.chat.web.controller

import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext

interface ChatController {

    suspend fun chatList(
        context: WebSocketSessionContext,
        rq: ChatListRequestDto,
    ): ChatListResponseDto

    suspend fun chatCreate(
        context: WebSocketSessionContext,
        rq: ChatCreateRequestDto,
    ): ChatCreateResponseDto

    suspend fun chatLeave(
        context: WebSocketSessionContext,
        rq: ChatLeaveRequestDto,
    ): ChatLeaveResponseDto

    suspend fun chatSubscribe(
        context: WebSocketSessionContext,
        rq: ChatSubscribeRequestDto,
    ): ChatSubscribeResponseDto

    suspend fun inviteList(
        context: WebSocketSessionContext,
        rq: InviteListRequestDto,
    ): InviteListResponseDto

    suspend fun inviteSend(
        context: WebSocketSessionContext,
        rq: InviteSendRequestDto,
    ): InviteSendResponseDto

    suspend fun inviteAccept(
        context: WebSocketSessionContext,
        rq: InviteAcceptRequestDto,
    ): InviteAcceptResponseDto
}
