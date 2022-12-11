package com.example.secure.chat.web.model.chat.processors

import com.example.secure.chat.web.model.chat.Author
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message

interface MessageProcessor {
    suspend fun MessageContext.processMessage()
}

data class MessageContext(
    val chat: Chat,
    val author: Author,
    val message: Message,
    val dispatch: (Message) -> Unit,
)
