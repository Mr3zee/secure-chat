package com.example.auth.common.chat

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class InviteDto(
    val chatId: Long,
    val encodedKey: RawBytesDto,
)
