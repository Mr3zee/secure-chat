package com.example.auth.common.on.connect

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val login: String,
) : OnConnectRequestDto
