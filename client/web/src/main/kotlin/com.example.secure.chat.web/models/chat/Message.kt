package com.example.secure.chat.web.models.chat

import androidx.compose.runtime.MutableState
import kotlinx.datetime.LocalDateTime

data class Message(
    val author: Author,
    val id: Long,
    val timestamp: LocalDateTime,
    val text: String,
    val status: MutableState<MessageStatus>,
)

enum class MessageStatus {
    Local, Verified, Failed, Unread
}
