package com.example.auth.common.action.client

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatMenuClientActionDto

@Serializable
data class OpenChatActionDto(
    val name: String,
) : ChatMenuClientActionDto

@Serializable
data class CreateNewChatActionDto(
    val name: String,
) : ChatMenuClientActionDto

@Serializable
data class AcceptInviteActionDto(
    val chatId: Long,
    val name: String,
) : ChatMenuClientActionDto
