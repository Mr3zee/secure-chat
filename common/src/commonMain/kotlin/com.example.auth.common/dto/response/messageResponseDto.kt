package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.message.MessageDto
import com.example.auth.common.dto.request.GetMessagesRequestDto
import com.example.auth.common.dto.request.SendMessageRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class GetMessageResponseDto(
    override val requestId: Long,
    val messages: List<MessageDto>,
    val hasMore: Boolean,
) : ServerResponseDto<GetMessagesRequestDto, GetMessageResponseDto>

@Serializable
data class SendMessageResponseDto(
    override val requestId: Long,
    val message: MessageDto,
) : ServerResponseDto<SendMessageRequestDto, SendMessageResponseDto>
