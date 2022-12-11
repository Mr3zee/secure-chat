package com.example.secure.chat.base.model.message

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class MessageCreateRq(
    val user: User,
    val text: ByteArrayWrapper,
)
