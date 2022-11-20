package com.example.auth.common.action.server

import com.example.auth.common.chat.ChatDto
import com.example.auth.common.chat.InviteDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatMenuServerActionDto

@Serializable
data class ChatListActionDto(
    val chats: List<ChatDto>,
) : ChatMenuServerActionDto

@Serializable
data class InviteListActionDto(
    val invites: List<InviteDto>,
) : ChatMenuServerActionDto

@Serializable
data class NewInviteActionDto(
    val inviteDto: InviteDto,
) : ChatMenuServerActionDto
