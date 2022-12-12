package com.example.auth.common.dto.model.message

import com.example.auth.common.dto.model.byte.Base64BytesDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageDraftDto(
    val chatId: Long,
    val encodedText: Base64BytesDto,
)
