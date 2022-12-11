package com.example.secure.chat.base.model.user

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class User(
    val id: Long,
    val login: String,
    val publicKey: ByteArrayWrapper,
)
