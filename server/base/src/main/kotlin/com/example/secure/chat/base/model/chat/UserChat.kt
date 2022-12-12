package com.example.secure.chat.base.model.chat

import com.example.secure.chat.base.model.wrapper.Base64Bytes

data class UserChat(
    val userId: Long,
    val chatId: Long,
    val name: Base64Bytes,
    val publicKey: Base64Bytes,
)
