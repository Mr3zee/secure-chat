package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import org.jetbrains.exposed.sql.Transaction

interface MessageRepository {
    fun Transaction.createMessage(rq: MessageCreateRq): Message
    fun Transaction.getMessages(chatId: Long): List<Message>
}
