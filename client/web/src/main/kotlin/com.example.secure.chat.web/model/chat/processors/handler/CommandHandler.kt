package com.example.secure.chat.web.model.chat.processors.handler

import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.processors.MessageContext

class CommandHandler<E : Enum<E>>(
    private val handlerCommand: String,
    private val handler: suspend MessageContext.(CommandMessage) -> E?,
) : Handler<E> {
    override suspend fun MessageContext.handle(message: Message): E? {
        val command = message.asCommand() ?: return null

        if (command.commandWord != handlerCommand) {
            return null
        }

        return handler(command)
    }

    private fun Message.asCommand(): CommandMessage? {
        if (text.startsWith("/")) {
            val (cmd, args) = text
                .drop(1)
                .split(" ")
                .let { it[0] to it.drop(1) }

            return CommandMessage(cmd, args)
        }

        return null
    }
}

data class CommandMessage(
    val commandWord: String,
    val args: List<String>,
)
