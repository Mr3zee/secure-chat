package com.example.secure.chat.web.model.chat.processors.handler

import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.processors.MessageContext

interface Handler<E : Enum<E>> {
    suspend fun MessageContext.handle(message: Message): E?
}
