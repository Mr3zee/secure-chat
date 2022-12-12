package com.example.secure.chat.base.model.invite

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import kotlinx.datetime.Instant

data class Invite(
    val id: Long,
    val userId: Long,
    val chatId: Long,
    val encodedKey: ByteArrayWrapper,
    val createdTs: Instant,
)
