package com.example.secure.chat.core.registry

import com.example.secure.chat.base.model.message.Message

interface NewMessagesRegistry : Registry<Message, Long> {

    override suspend fun subscribe(
        entityId: Long,
        sessionId: Long,
        action: suspend (List<Message>) -> Boolean,
    )
}
