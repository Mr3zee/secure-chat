package com.example.secure.chat.web.model.chat.processors

import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.chat.Message

class GlobalMessageProcessor(private val model: ChatModel) : MessageProcessor {
    override suspend fun MessageContext.processMessage(message: Message): Unit = with(model.localMessageProcessor) {
        processMessage(message)
    }

    override suspend fun createMessage(text: String): Message = with(model.localMessageProcessor) {
        createMessage(text)
    }

    companion object {
        val CMD_REFERENCE = """
/invite <username> - invite new members. 
/leave - leave chat.
        """.trimIndent()
    }
}
