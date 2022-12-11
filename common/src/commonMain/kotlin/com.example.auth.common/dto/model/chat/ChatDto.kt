package com.example.auth.common.dto.model.chat

import com.example.auth.common.dto.model.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val id: Long,
    val name: RawBytesDto,
)
