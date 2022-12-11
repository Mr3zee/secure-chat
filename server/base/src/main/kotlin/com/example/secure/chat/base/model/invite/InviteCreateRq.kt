package com.example.secure.chat.base.model.invite

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class InviteCreateRq(
    val userLogin: String,
    val chatId: Long,
    val encodedKey: ByteArrayWrapper,
)
