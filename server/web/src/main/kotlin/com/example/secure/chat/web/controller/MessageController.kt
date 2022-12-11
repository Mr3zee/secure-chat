package com.example.secure.chat.web.controller

import com.example.auth.common.dto.request.MessageListRequestDto
import com.example.auth.common.dto.request.MessageSendRequestDto
import com.example.auth.common.dto.response.MessageListResponseDto
import com.example.auth.common.dto.response.MessageSendResponseDto
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext

interface MessageController {

    fun messageList(
        context: WebSocketSessionContext,
        rq: MessageListRequestDto,
    ): MessageListResponseDto

    fun messageSend(
        context: WebSocketSessionContext,
        rq: MessageSendRequestDto,
    ): MessageSendResponseDto
}
