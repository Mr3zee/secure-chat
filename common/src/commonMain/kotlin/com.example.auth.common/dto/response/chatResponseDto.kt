package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.chat.ChatDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.request.AcceptInviteRequestDto
import com.example.auth.common.dto.request.ChatListRequestDto
import com.example.auth.common.dto.request.CreateChatRequestDto
import com.example.auth.common.dto.request.InviteListRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatListResponseDto(
    override val requestId: Long,
    val chats: List<ChatDto>,
) : ServerResponseDto<ChatListRequestDto, ChatListResponseDto>

@Serializable
data class InviteListResponseDto(
    override val requestId: Long,
    val invites: List<InviteDto>,
) : ServerResponseDto<InviteListRequestDto, InviteListResponseDto>

@Serializable
data class AcceptInviteResponseDto(
    override val requestId: Long,
    val acceptedChat: ChatDto,
) : ServerResponseDto<AcceptInviteRequestDto, AcceptInviteResponseDto>

@Serializable
data class CreateChatResponseDto(
    override val requestId: Long,
    val chat: ChatDto,
) : ServerResponseDto<CreateChatRequestDto, CreateChatResponseDto>
