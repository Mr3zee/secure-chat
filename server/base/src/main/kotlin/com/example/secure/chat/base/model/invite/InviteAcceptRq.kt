package com.example.secure.chat.base.model.invite

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.wrapper.Base64Bytes

data class InviteAcceptRq(
    val user: User,
    val chatId: Long,
    val chatName: Base64Bytes,
)
