package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.auth.common.dto.request.CheckDecodedMessageRequestDto
import com.example.auth.common.dto.request.LoginRequestDto
import com.example.auth.common.dto.request.LogoutRequestDto
import com.example.auth.common.dto.request.RegisterRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val encodedMessage: Base64BytesDto,
) : AuthenticationResponseDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterResponseDto(
    val publicKey: Base64BytesDto,
) : AuthenticationResponseDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageResponseDto(
    val publicKey: Base64BytesDto,
) : AuthenticationResponseDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutResponseDto(
    override val requestId: Long,
) : ServerResponseDto<LogoutRequestDto, LogoutResponseDto>
