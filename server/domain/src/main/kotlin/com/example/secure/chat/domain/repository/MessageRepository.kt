package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.domain.db.util.Transactional

interface MessageRepository {
    fun Transactional.createMessage(rq: MessageCreateRq): Long
    fun Transactional.getMessage(id: Long): Message
    fun Transactional.getMessages(chatId: Long, idLt: Long, limit: Int): List<Message>
}
