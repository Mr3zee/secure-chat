package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.auth.common.dto.response.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val userLogin: String,
) : AuthenticationRequestDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterRequestDto(
    val userLogin: String,
    val publicKey: Base64BytesDto,
) : AuthenticationRequestDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageRequestDto(
    val userLogin: String,
    val decodedMessage: Base64BytesDto,
) : AuthenticationRequestDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutRequestDto(
    override val requestId: Long,
) : ClientRequestDto<LogoutRequestDto, LogoutResponseDto>
