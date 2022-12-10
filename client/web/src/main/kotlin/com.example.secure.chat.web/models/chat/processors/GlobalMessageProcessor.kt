package com.example.secure.chat.web.models.chat.processors

import com.example.secure.chat.web.models.chat.Message

object GlobalMessageProcessor : MessageProcessor {
    override suspend fun processMessage(message: Message): Message {
        return LocalMessageProcessor.processMessage(message)
    }
}
