package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.message.MessageDraftDto
import com.example.auth.common.dto.response.MessageListResponseDto
import com.example.auth.common.dto.response.MessageSendResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageListRequestDto(
    override val requestId: Long,
    val chatId: Long,
    val lastMessageId: Long?,
    val limit: Int,
) : ClientRequestDto<MessageListRequestDto, MessageListResponseDto>

@Serializable
data class MessageSendRequestDto(
    override val requestId: Long,
    val message: MessageDraftDto,
) : ClientRequestDto<MessageSendRequestDto, MessageSendResponseDto>
