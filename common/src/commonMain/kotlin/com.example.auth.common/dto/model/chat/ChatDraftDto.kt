package com.example.auth.common.dto.model.chat

import com.example.auth.common.dto.model.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatDraftDto(
    val name: RawBytesDto,
)
