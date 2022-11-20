package com.example.auth.common.message

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class MessageCreateRqDto(
    val text: RawBytesDto,
)
