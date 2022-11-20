package com.example.auth.common.on.connect

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRqDto(
    val login: String,
    val publicKey: String,
) : OnConnectRqDto
