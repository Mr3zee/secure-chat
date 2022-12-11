package com.example.secure.chat.core.service.impl

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.core.service.MessageService
import com.example.secure.chat.domain.db.util.tx
import com.example.secure.chat.domain.repository.MessageRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessageServiceImpl : MessageService, KoinComponent {

    private val messageRepository by inject<MessageRepository>()

    override suspend fun getMessages(
        chatId: Long,
        idLt: Long?,
        limit: Int,
    ): Pair<List<Message>, Boolean> = tx {
        with(messageRepository) {
            getMessages(chatId, idLt ?: 0, limit + 1)
        }.let { result ->
            result.take(limit) to (result.size > limit)
        }
    }

    override suspend fun sendMessage(rq: MessageCreateRq): Message = tx {
        with(messageRepository) {
            val messageId = createMessage(rq)
            getMessage(messageId)
        }
    }
}
