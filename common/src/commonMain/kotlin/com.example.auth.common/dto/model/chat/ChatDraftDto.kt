package com.example.auth.common.dto.model.chat

import com.example.auth.common.dto.model.byte.Base64BytesDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatDraftDto(
    val name: Base64BytesDto,
    val publicKey: Base64BytesDto,
)
