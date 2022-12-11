package com.example.auth.common.dto.request

import com.example.auth.common.dto.response.GetUserPublicKeyResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class GetUserPublicKeyRequestDto(
    override val requestId: Long,
    val userLogin: String,
) : ClientRequestDto<GetUserPublicKeyRequestDto, GetUserPublicKeyResponseDto>
