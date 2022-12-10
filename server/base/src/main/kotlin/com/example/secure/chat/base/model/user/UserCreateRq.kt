package com.example.secure.chat.base.model.user

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class UserCreateRq(
    val login: String,
    val publicKey: ByteArrayWrapper,
)
