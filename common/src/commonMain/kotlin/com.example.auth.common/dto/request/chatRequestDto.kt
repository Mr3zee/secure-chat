package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.byte.RawBytesDto
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
    val startMessageText: RawBytesDto,
) : ClientRequestDto<ChatCreateRequestDto, ChatCreateResponseDto>

@Serializable
data class InviteListRequestDto(
    override val requestId: Long,
) : ClientRequestDto<InviteListRequestDto, InviteListResponseDto>

@Serializable
data class InviteAcceptRequestDto(
    override val requestId: Long,
    val invite: InviteDto,
    val chatName: RawBytesDto,
) : ClientRequestDto<InviteAcceptRequestDto, InviteAcceptResponseDto>

@Serializable
data class InviteSendRequestDto(
    override val requestId: Long,
    val userLogin: String,
    val invite: InviteDraftDto,
) : ClientRequestDto<InviteSendRequestDto, InviteSendResponseDto>
