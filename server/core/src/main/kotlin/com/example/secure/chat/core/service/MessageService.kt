package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq

interface MessageService {

    suspend fun getMessages(chatId: Long, idLt: Long?, limit: Int): Pair<List<Message>, Boolean>

    suspend fun sendMessage(rq: MessageCreateRq): Message
}
