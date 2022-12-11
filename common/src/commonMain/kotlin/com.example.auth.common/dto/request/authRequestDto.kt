package com.example.auth.common.dto.request

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.response.CheckDecodedMessageResponseDto
import com.example.auth.common.dto.response.LoginResponseDto
import com.example.auth.common.dto.response.LogoutResponseDto
import com.example.auth.common.dto.response.RegisterResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    override val requestId: Long,
    val userLogin: String,
) : ClientRequestDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterRequestDto(
    override val requestId: Long,
    val userLogin: String,
    val publicKey: RawBytesDto,
) : ClientRequestDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageRequestDto(
    override val requestId: Long,
    val userLogin: String,
    val publicKey: RawBytesDto,
) : ClientRequestDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutRequestDto(
    override val requestId: Long,
) : ClientRequestDto<LogoutRequestDto, LogoutResponseDto>
