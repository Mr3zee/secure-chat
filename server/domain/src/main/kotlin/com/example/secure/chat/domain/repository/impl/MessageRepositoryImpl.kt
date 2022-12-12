package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.tables.MessageTables.Messages
import com.example.secure.chat.domain.db.tables.UserTables.Users
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.MessageRepository
import org.jetbrains.exposed.sql.*

object MessageRepositoryImpl : MessageRepository {

    override fun Transactional.createMessage(rq: MessageCreateRq): Long =
        Messages.insertAndGetId {
            it[chatId] = rq.user.id
            it[userId] = rq.user.id
            it[text] = rq.text.byteArray
        }.value

    override fun Transactional.getMessage(id: Long): Message =
        Messages.innerJoin(Users).select {
            Messages.id.eq(id)
        }.single().let { row ->
            Message(
                row[Messages.id].value,
                row[Messages.chatId],
                row[Users.login],
                row[Messages.text].let(::ByteArrayWrapper),
                row[Messages.createdTs],
            )
        }

    override fun Transactional.getMessages(chatId: Long, idLt: Long, limit: Int): List<Message> =
        Messages.innerJoin(Users).select {
            Messages.chatId.eq(chatId)
                .and(Messages.id.less(chatId))
        }.orderBy(
            Messages.id,
            SortOrder.DESC,
        ).limit(limit).map { row ->
            Message(
                row[Messages.id].value,
                row[Messages.chatId],
                row[Users.login],
                row[Messages.text].let(::ByteArrayWrapper),
                row[Messages.createdTs],
            )
        }

    override fun Transactional.getNewMessages(idGt: Long, limit: Int): List<Message> =
        Messages.select {
            Messages.id.greater(idGt)
        }.orderBy(
            Messages.id,
            SortOrder.ASC,
        ).limit(limit).map { row ->
            Message(
                row[Messages.id].value,
                row[Messages.chatId],
                row[Users.login],
                row[Messages.text].let(::ByteArrayWrapper),
                row[Messages.createdTs],
            )
        }

    override fun Transactional.getLastMessageId(): Long =
        Messages.slice(Messages.id.max())
            .selectAll()
            .singleOrNull()?.get(Messages.id.max()) ?: 0L
}
