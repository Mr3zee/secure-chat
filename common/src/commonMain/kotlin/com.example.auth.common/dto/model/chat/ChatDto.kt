package com.example.auth.common.dto.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val id: Long,
    val name: String,
)
