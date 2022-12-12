package com.example.secure.chat.base.model.message

import com.example.secure.chat.base.model.wrapper.Base64Bytes
import kotlinx.datetime.Instant

data class Message(
    val id: Long,
    val chatId: Long,
    val userLogin: String,
    val text: Base64Bytes,
    val createdTs: Instant,
)
