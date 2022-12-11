package com.example.secure.chat.base.model.message

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import kotlinx.datetime.Instant

data class Message(
    val id: Long,
    val chatId: Long,
    val userLogin: String,
    val text: ByteArrayWrapper,
    val createdTs: Instant,
)
