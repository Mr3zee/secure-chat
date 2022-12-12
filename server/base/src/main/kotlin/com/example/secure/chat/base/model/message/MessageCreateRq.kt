package com.example.secure.chat.base.model.message

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.wrapper.Base64Bytes

data class MessageCreateRq(
    val user: User,
    val chatId: Long,
    val text: Base64Bytes,
)
