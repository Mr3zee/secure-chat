package com.example.auth.common.dto.model.message

import com.example.auth.common.dto.model.byte.RawBytesDto
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Long,
    val chatId: Long,
    val encodedText: RawBytesDto,
    val timestamp: Instant,
)
