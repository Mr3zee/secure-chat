package com.example.auth.common.action.server

import com.example.auth.common.message.MessageDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface CurrentChatServerActionDto

@Serializable
data class ChatMessagesActionDto(
    val messages: List<MessageDto>,
    val hasMore: Boolean,
) : CurrentChatServerActionDto

@Serializable
data class NewMessageActionDto(
    val message: MessageDto,
) : CurrentChatServerActionDto
