package com.example.auth.common.action.server

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthenticationServerActionDto

@Serializable
data class DecodingRequestActionDto(
    val value: RawBytesDto,
) : AuthenticationServerActionDto
