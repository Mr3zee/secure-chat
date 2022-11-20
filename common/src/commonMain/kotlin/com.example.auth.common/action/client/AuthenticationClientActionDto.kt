package com.example.auth.common.action.client

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthenticationClientActionDto

@Serializable
data class LoginActionDto(
    val login: String,
) : AuthenticationClientActionDto

@Serializable
data class RegisterActionDto(
    val login: String,
    val publicKey: RawBytesDto,
) : AuthenticationClientActionDto

@Serializable
data class DecodedResponseActionDto(
    val value: RawBytesDto,
) : AuthenticationClientActionDto

@Serializable
object LogoutActionDto : AuthenticationClientActionDto
