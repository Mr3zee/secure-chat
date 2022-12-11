package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.message.MessageDraftDto
import com.example.auth.common.dto.response.GetMessageResponseDto
import com.example.auth.common.dto.response.SendMessageResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class GetMessagesRequestDto(
    override val requestId: Long,
    val chatId: Long,
    val lastMessageId: Long?,
    val limit: Int,
) : ClientRequestDto<GetMessagesRequestDto, GetMessageResponseDto>

@Serializable
data class SendMessageRequestDto(
    override val requestId: Long,
    val message: MessageDraftDto,
) : ClientRequestDto<SendMessageRequestDto, SendMessageResponseDto>
