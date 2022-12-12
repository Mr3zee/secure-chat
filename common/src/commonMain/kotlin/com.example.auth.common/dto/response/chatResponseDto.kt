package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.chat.ChatDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.model.message.MessageDto
import com.example.auth.common.dto.request.*
import kotlinx.serialization.Serializable

@Serializable
data class ChatListResponseDto(
    override val requestId: Long,
    val chats: List<ChatDto>,
) : ServerResponseDto<ChatListRequestDto, ChatListResponseDto>

@Serializable
data class ChatCreateResponseDto(
    override val requestId: Long,
    val chat: ChatDto,
    val startMessage: MessageDto,
) : ServerResponseDto<ChatCreateRequestDto, ChatCreateResponseDto>

@Serializable
data class ChatLeaveResponseDto(
    override val requestId: Long,
) : ServerResponseDto<ChatLeaveRequestDto, ChatLeaveResponseDto>

@Serializable
data class ChatSubscribeResponseDto(
    override val requestId: Long,
) : ServerResponseDto<ChatSubscribeRequestDto, ChatSubscribeResponseDto>

@Serializable
data class InviteListResponseDto(
    override val requestId: Long,
    val invites: List<InviteDto>,
) : ServerResponseDto<InviteListRequestDto, InviteListResponseDto>

@Serializable
data class InviteAcceptResponseDto(
    override val requestId: Long,
    val acceptedChat: ChatDto,
) : ServerResponseDto<InviteAcceptRequestDto, InviteAcceptResponseDto>

@Serializable
data class InviteSendResponseDto(
    override val requestId: Long,
) : ServerResponseDto<InviteSendRequestDto, InviteSendResponseDto>
