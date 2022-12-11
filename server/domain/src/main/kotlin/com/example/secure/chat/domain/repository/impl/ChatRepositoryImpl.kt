package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.JoinTables.UsersChatsJoinTable
import com.example.secure.chat.base.model.chat.ChatCreateRq
import com.example.secure.chat.base.model.chat.UsersChat
import com.example.secure.chat.domain.repository.ChatRepository
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object ChatRepositoryImpl : ChatRepository {

    override fun Transaction.createChat(rq: ChatCreateRq) {
        val chat = Chats.insertAndGetId { }.value
        UsersChatsJoinTable.insert {
            it[userId] = rq.userId
            it[chatId] = chat
            it[name] = rq.name
        }
    }

    override fun Transaction.getUsersChats(userId: Long): List<UsersChat> =
        UsersChatsJoinTable.select {
            UsersChatsJoinTable.userId.eq(userId)
        }.map { row ->
            UsersChat(
                row[UsersChatsJoinTable.userId],
                row[UsersChatsJoinTable.chatId],
                row[UsersChatsJoinTable.name],
            )
        }
}
