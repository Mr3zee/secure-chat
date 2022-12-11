package com.example.secure.chat.web.controller

import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext

interface ChatController {

    fun chatList(
        context: WebSocketSessionContext,
        rq: ChatListRequestDto,
    ): ChatListResponseDto

    fun chatCreate(
        context: WebSocketSessionContext,
        rq: ChatCreateRequestDto,
    ): ChatCreateResponseDto

    fun inviteList(
        context: WebSocketSessionContext,
        rq: InviteListRequestDto,
    ): InviteListResponseDto

    fun inviteSend(
        context: WebSocketSessionContext,
        rq: InviteSendRequestDto,
    ): InviteSendResponseDto

    fun inviteAccept(
        context: WebSocketSessionContext,
        rq: InviteAcceptRequestDto,
    ): InviteAcceptResponseDto
}
