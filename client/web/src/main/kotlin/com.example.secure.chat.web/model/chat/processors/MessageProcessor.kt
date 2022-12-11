package com.example.secure.chat.web.model.chat.processors

import com.example.secure.chat.web.model.chat.Author
import com.example.secure.chat.web.model.chat.Message

interface MessageProcessor {
    suspend fun MessageContext.processMessage(message: Message)

    suspend fun createMessage(text: String): Message
}

data class MessageContext(
    val author: Author,
    val dispatch: (Message) -> Unit,
)
