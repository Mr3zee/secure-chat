package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.request.CheckDecodedMessageRequestDto
import com.example.auth.common.dto.request.LoginRequestDto
import com.example.auth.common.dto.request.LogoutRequestDto
import com.example.auth.common.dto.request.RegisterRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val encodedMessage: RawBytesDto,
) : AuthenticationResponseDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterResponseDto(
    val publicKey: RawBytesDto,
) : AuthenticationResponseDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageResponseDto(
    val publicKey: RawBytesDto,
) : AuthenticationResponseDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutResponseDto(
    override val requestId: Long,
) : ServerResponseDto<LogoutRequestDto, LogoutResponseDto>
