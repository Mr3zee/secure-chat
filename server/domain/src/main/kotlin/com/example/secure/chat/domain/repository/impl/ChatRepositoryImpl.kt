package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.JoinTables.UsersChatsJoinTable
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.domain.repository.ChatRepository
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object ChatRepositoryImpl : ChatRepository {

    override fun Transaction.createChat(): Long =
        Chats.insertAndGetId { }.value

    override fun Transaction.createUserChat(rqChatId: Long, rq: UserChatCreateRq): UserChat =
        UsersChatsJoinTable.insert {
            it[userId] = rq.userId
            it[chatId] = rqChatId
            it[name] = rq.name
        }.let { UserChat(rq.userId, rqChatId, rq.name) }

    override fun Transaction.getUsersChats(userId: Long): List<UserChat> =
        UsersChatsJoinTable.select {
            UsersChatsJoinTable.userId.eq(userId)
        }.map { row ->
            UserChat(
                row[UsersChatsJoinTable.userId],
                row[UsersChatsJoinTable.chatId],
                row[UsersChatsJoinTable.name],
            )
        }
}
