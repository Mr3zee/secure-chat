package com.example.secure.chat.base.model.user

import com.example.secure.chat.base.model.wrapper.Base64Bytes

data class User(
    val id: Long,
    val login: String,
    val publicKey: Base64Bytes,
)
