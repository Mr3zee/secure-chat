package com.example.auth.common.dto.model.invite

import com.example.auth.common.dto.model.byte.Base64BytesDto
import kotlinx.serialization.Serializable

@Serializable
data class InviteDraftDto(
    val userLogin: String,
    val chatId: Long,
    val encodedKey: Base64BytesDto,
)
