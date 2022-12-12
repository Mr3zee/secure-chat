package com.example.auth.common.dto.model.message

import com.example.auth.common.dto.model.byte.Base64BytesDto
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Long,
    val chatId: Long,
    val userLogin: String,
    val encodedText: Base64BytesDto,
    val timestamp: Instant,
)
