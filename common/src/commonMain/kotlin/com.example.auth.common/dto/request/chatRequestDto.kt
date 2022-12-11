package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.chat.ChatDraftDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.response.AcceptInviteResponseDto
import com.example.auth.common.dto.response.ChatListResponseDto
import com.example.auth.common.dto.response.CreateChatResponseDto
import com.example.auth.common.dto.response.InviteListResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatListRequestDto(
    override val requestId: Long,
) : ClientRequestDto<ChatListRequestDto, ChatListResponseDto>

@Serializable
data class InviteListRequestDto(
    override val requestId: Long,
) : ClientRequestDto<InviteListRequestDto, InviteListResponseDto>

@Serializable
data class AcceptInviteRequestDto(
    override val requestId: Long,
    val invite: InviteDto,
    val chatName: String,
) : ClientRequestDto<AcceptInviteRequestDto, AcceptInviteResponseDto>

@Serializable
data class CreateChatRequestDto(
    override val requestId: Long,
    val chat: ChatDraftDto,
) : ClientRequestDto<CreateChatRequestDto, CreateChatResponseDto>
