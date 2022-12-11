package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.request.CheckDecodedMessageRequestDto
import com.example.auth.common.dto.request.LoginRequestDto
import com.example.auth.common.dto.request.LogoutRequestDto
import com.example.auth.common.dto.request.RegisterRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    override val requestId: Long,
    val textToDecode: RawBytesDto,
) : ServerResponseDto<LoginRequestDto, LoginResponseDto>

@Serializable
data class RegisterResponseDto(
    override val requestId: Long,
) : ServerResponseDto<RegisterRequestDto, RegisterResponseDto>

@Serializable
data class CheckDecodedMessageResponseDto(
    override val requestId: Long,
) : ServerResponseDto<CheckDecodedMessageRequestDto, CheckDecodedMessageResponseDto>

@Serializable
data class LogoutResponseDto(
    override val requestId: Long,
) : ServerResponseDto<LogoutRequestDto, LogoutResponseDto>
