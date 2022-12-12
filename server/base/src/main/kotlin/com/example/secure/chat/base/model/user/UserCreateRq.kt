package com.example.secure.chat.base.model.user

import com.example.secure.chat.base.model.wrapper.Base64Bytes

data class UserCreateRq(
    val login: String,
    val publicKey: Base64Bytes,
)
