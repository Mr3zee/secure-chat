package com.example.auth.common.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val id: Long,
    val name: String,
)
