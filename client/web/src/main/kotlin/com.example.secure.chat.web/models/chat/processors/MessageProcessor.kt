package com.example.secure.chat.web.models.chat.processors

import com.example.secure.chat.web.models.chat.Message

interface MessageProcessor {
    suspend fun processMessage(message: Message): Message
}
