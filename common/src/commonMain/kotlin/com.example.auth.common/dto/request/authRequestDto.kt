package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.response.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val userLogin: String,
) : AuthenticationRequestDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterRequestDto(
    val userLogin: String,
    val publicKey: RawBytesDto,
) : AuthenticationRequestDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageRequestDto(
    val userLogin: String,
    val decodedMessage: RawBytesDto,
) : AuthenticationRequestDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutRequestDto(
    override val requestId: Long,
) : ClientRequestDto<LogoutRequestDto, LogoutResponseDto>
