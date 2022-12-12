package com.example.secure.chat.base.model.invite

import com.example.secure.chat.base.model.wrapper.Base64Bytes
import kotlinx.datetime.Instant

data class Invite(
    val id: Long,
    val userId: Long,
    val chatId: Long,
    val encodedKey: Base64Bytes,
    val createdTs: Instant,
)
