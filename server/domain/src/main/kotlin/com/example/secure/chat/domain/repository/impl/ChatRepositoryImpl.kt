package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.JoinTables.UsersChatsJoinTable
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.ChatRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object ChatRepositoryImpl : ChatRepository {

    override fun Transactional.createChat(): Long =
        Chats.insertAndGetId { }.value

    override fun Transactional.createUserChat(rqChatId: Long, rq: UserChatCreateRq): UserChat =
        UsersChatsJoinTable.insert {
            it[userId] = rq.user.id
            it[chatId] = rqChatId
            it[name] = rq.name.byteArray
        }.let { UserChat(rq.user.id, rqChatId, rq.name) }

    override fun Transactional.getUsersChats(userId: Long): List<UserChat> =
        UsersChatsJoinTable.select {
            UsersChatsJoinTable.userId.eq(userId)
        }.map { row ->
            UserChat(
                row[UsersChatsJoinTable.userId],
                row[UsersChatsJoinTable.chatId],
                ByteArrayWrapper(row[UsersChatsJoinTable.name]),
            )
        }
}
