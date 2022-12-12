package com.example.secure.chat.core.registry.impl

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.core.registry.NewMessagesRegistry
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.db.util.tx
import com.example.secure.chat.domain.repository.MessageRepository
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NewMessagesRegistryImpl : AbstractRegistryImpl<Message, Long, Long>(), NewMessagesRegistry, KoinComponent {

    private val messageRepository by inject<MessageRepository>()

    override val logger = KotlinLogging.logger { }

    override fun Message.entityId(): Long = chatId
    override fun Message.eventId(): Long = id

    override suspend fun Transactional.loadLastEventId(): Long = tx {
        with(messageRepository) { getLastMessageId() }
    }

    override suspend fun Transactional.loadNewEvents(
        lastEventId: Long,
    ): List<Message> = tx {
        with(messageRepository) { getNewMessages(lastEventId, 1000) }
    }
}
