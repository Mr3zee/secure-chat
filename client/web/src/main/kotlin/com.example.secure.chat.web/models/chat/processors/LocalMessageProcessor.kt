package com.example.secure.chat.web.models.chat.processors

import androidx.compose.runtime.mutableStateOf
import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.models.chat.MessageStatus

object LocalMessageProcessor : MessageProcessor {
    private val echoBot = Author.Bot("Echo")

    override suspend fun processMessage(message: Message): Message {
        return when (message) {
            is Message.Text -> Message.Text(
                author = echoBot,
                id = message.id,
                timestamp = message.timestamp,
                text = message.text,
                status = mutableStateOf(MessageStatus.Verified)
            )

            is Message.Command -> Message.Command(
                author = echoBot,
                id = message.id,
                timestamp = message.timestamp,
                text = message.text,
                status = mutableStateOf(MessageStatus.Verified),
                command = message.command,
                arguments = message.arguments,
            )
        }
    }
}
