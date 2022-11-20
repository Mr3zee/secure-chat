package com.example.auth.common.action.client

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface CurrentChatClientActionDto

@Serializable
data class GetMessagesActionDto(
    val lastMessageId: Long?,
    val limit: Boolean,
) : CurrentChatClientActionDto

@Serializable
data class SendMessageActionDto(
    val message: RawBytesDto,
) : CurrentChatClientActionDto

@Serializable
object LeaveChatActionDto : CurrentChatClientActionDto

@Serializable
data class InviteUserActionDto(
    val login: String,
) : CurrentChatClientActionDto
