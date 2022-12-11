package com.example.secure.chat.base.model.chat

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class UserChat(
    val userId: Long,
    val chatId: Long,
    val name: ByteArrayWrapper,
    val publicKey: ByteArrayWrapper,
)
