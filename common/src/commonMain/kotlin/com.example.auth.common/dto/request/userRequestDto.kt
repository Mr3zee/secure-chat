package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.invite.InviteDto
import com.example.auth.common.dto.response.GetUserPublicKeyResponseDto
import com.example.auth.common.dto.response.SendInviteResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class GetUserPublicKeyRequestDto(
    override val requestId: Long,
    val userLogin: String,
) : ClientRequestDto<GetUserPublicKeyRequestDto, GetUserPublicKeyResponseDto>

@Serializable
data class SendInviteRequestDto(
    override val requestId: Long,
    val userLogin: String,
    val invite: InviteDto,
) : ClientRequestDto<SendInviteRequestDto, SendInviteResponseDto>
