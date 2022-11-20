package com.example.auth.common.on.connect

import kotlinx.serialization.Serializable

@Serializable
data class LoginRqDto(
    val login: String,
) : OnConnectRqDto
