package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.auth.common.dto.model.chat.ChatDraftDto
import com.example.auth.common.dto.model.invite.InviteDraftDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ChatListRequestDto(
    override val requestId: Long,
) : ClientRequestDto<ChatListRequestDto, ChatListResponseDto>

@Serializable
data class ChatCreateRequestDto(
    override val requestId: Long,
    val chat: ChatDraftDto,
    val startMessageText: Base64BytesDto,
) : ClientRequestDto<ChatCreateRequestDto, ChatCreateResponseDto>

@Serializable
data class ChatLeaveRequestDto(
    override val requestId: Long,
    val chatId: Long,
) : ClientRequestDto<ChatLeaveRequestDto, ChatLeaveResponseDto>

@Serializable
data class ChatSubscribeRequestDto(
    override val requestId: Long,
    val chatId: Long,
) : ClientRequestDto<ChatSubscribeRequestDto, ChatSubscribeResponseDto>

@Serializable
data class InviteListRequestDto(
    override val requestId: Long,
) : ClientRequestDto<InviteListRequestDto, InviteListResponseDto>

@Serializable
data class InviteAcceptRequestDto(
    override val requestId: Long,
    val invite: InviteDto,
    val chatName: Base64BytesDto,
) : ClientRequestDto<InviteAcceptRequestDto, InviteAcceptResponseDto>

@Serializable
data class InviteSendRequestDto(
    override val requestId: Long,
    val userLogin: String,
    val invite: InviteDraftDto,
) : ClientRequestDto<InviteSendRequestDto, InviteSendResponseDto>
