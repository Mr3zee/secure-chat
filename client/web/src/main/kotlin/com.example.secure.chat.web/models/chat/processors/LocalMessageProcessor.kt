package com.example.secure.chat.web.models.chat.processors

import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Message

object LocalMessageProcessor : MessageProcessor {
    private val echoBot = Author.Bot("Echo")

    override suspend fun processMessage(message: Message): Message {
        return when (message) {
            is Message.Text -> Message.Text(
                author = echoBot,
                id = message.id,
                timestamp = message.timestamp,
                text = message.text
            )

            is Message.Command -> Message.Command(
                author = echoBot,
                id = message.id,
                timestamp = message.timestamp,
                text = message.text,
                command = message.command,
                arguments = message.arguments
            )
        }
    }
}
