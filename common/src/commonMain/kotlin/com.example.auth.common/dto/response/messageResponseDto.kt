package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.auth.common.dto.model.message.MessageDto
import com.example.auth.common.dto.request.MessageListRequestDto
import com.example.auth.common.dto.request.MessageSendRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageListResponseDto(
    override val requestId: Long,
    val messages: List<MessageDto>,
    val publicKey: Base64BytesDto,
    val hasMore: Boolean,
) : ServerResponseDto<MessageListRequestDto, MessageListResponseDto>

@Serializable
data class MessageSendResponseDto(
    override val requestId: Long,
    val message: MessageDto,
) : ServerResponseDto<MessageSendRequestDto, MessageSendResponseDto>
