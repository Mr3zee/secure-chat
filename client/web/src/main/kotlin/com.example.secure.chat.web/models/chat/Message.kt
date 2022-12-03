package com.example.secure.chat.web.models.chat

import androidx.compose.runtime.MutableState
import kotlinx.datetime.LocalDateTime

sealed interface Message {
    val author: Author
    val id: Long
    val timestamp: LocalDateTime
    val text: String
    val status: MutableState<MessageStatus>

    data class Text(
        override val author: Author,
        override val id: Long,
        override val timestamp: LocalDateTime,
        override val text: String,
        override val status: MutableState<MessageStatus>,
    ) : Message

    data class Command(
        override val author: Author,
        override val id: Long,
        override val timestamp: LocalDateTime,
        override val text: String,
        override val status: MutableState<MessageStatus>,
        val command: String,
        val arguments: List<String>
    ) : Message
}

enum class MessageStatus {
    Local, Verified, Failed
}
