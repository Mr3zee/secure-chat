package com.example.auth.common.message

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Long,
    val text: RawBytesDto,
)
