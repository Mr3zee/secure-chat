package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq

interface MessageService {

    fun getMessages(chatId: Long, idLt: Long?, limit: Int): Pair<List<Message>, Boolean>

    fun sendMessage(rq: MessageCreateRq): Message
}
