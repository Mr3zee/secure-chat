package com.example.secure.chat.base.model.chat

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class UserChatCreateRq(
    val user: User,
    val name: ByteArrayWrapper,
)
