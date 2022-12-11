package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.MessageTables.Messages
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.tables.UserTables.Users
import com.example.secure.chat.domain.repository.MessageRepository
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object MessageRepositoryImpl : MessageRepository {

    override fun Transaction.createMessage(rq: MessageCreateRq): Long =
        Messages.insertAndGetId {
            it[chatId] = rq.user.id
            it[userId] = rq.user.id
            it[text] = rq.text.byteArray
        }.value

    override fun Transaction.getMessage(id: Long): Message =
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

    override fun Transaction.getMessages(chatId: Long): List<Message> =
        Messages.innerJoin(Users).select {
            Messages.chatId.eq(chatId)
        }.map { row ->
            Message(
                row[Messages.id].value,
                row[Messages.chatId],
                row[Users.login],
                row[Messages.text].let(::ByteArrayWrapper),
                row[Messages.createdTs],
            )
        }
}
