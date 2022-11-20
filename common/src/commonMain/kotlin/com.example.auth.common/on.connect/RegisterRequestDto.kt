package com.example.auth.common.on.connect

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val login: String,
    val publicKey: String,
) : OnConnectRequestDto
