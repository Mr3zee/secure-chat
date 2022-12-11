package com.example.auth.common.dto.model.message

import com.example.auth.common.dto.model.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageDraftDto(
    val chatId: Long,
    val encodedText: RawBytesDto,
)
