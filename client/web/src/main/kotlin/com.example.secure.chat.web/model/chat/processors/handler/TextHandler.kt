package com.example.secure.chat.web.model.chat.processors.handler

import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.processors.MessageContext

class TextHandler<E : Enum<E>>(private val handler: suspend MessageContext.(Message) -> E?) : Handler<E> {
    override suspend fun MessageContext.handle(message: Message): E? {
        return handler(message)
    }
}
