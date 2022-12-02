package com.example.secure.chat.web.models.chat

import kotlinx.datetime.LocalDateTime

sealed interface Message {
    val author: Author
    val id: Long
    val timestamp: LocalDateTime
    val text: String

    data class Text(
        override val author: Author,
        override val id: Long,
        override val timestamp: LocalDateTime,
        override val text: String
    ) : Message

    data class Command(
        override val author: Author,
        override val id: Long,
        override val timestamp: LocalDateTime,
        override val text: String,
        val command: String,
        val arguments: List<String>
    ) : Message
}
