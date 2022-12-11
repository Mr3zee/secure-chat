package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.request.GetUserPublicKeyRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class GetUserPublicKeyResponseDto(
    override val requestId: Long,
    val publicKey: RawBytesDto?,
) : ServerResponseDto<GetUserPublicKeyRequestDto, GetUserPublicKeyResponseDto>
