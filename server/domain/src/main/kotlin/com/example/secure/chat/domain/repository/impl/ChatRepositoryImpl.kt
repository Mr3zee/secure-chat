package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.JoinTables.UsersChatsJoinTable
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.ChatRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object ChatRepositoryImpl : ChatRepository {

    override fun Transactional.createChat(rqPublicKey: Base64Bytes): Long =
        Chats.insertAndGetId {
            it[publicKey] = rqPublicKey.content
        }.value

    override fun Transactional.createUserChat(rqChatId: Long, rq: UserChatCreateRq) {
        UsersChatsJoinTable.insert {
            it[userId] = rq.user.id
            it[chatId] = rqChatId
            it[name] = rq.name.content
        }
    }

    override fun Transactional.getUserChat(chatId: Long, userId: Long): UserChat {
        return UsersChatsJoinTable.innerJoin(Chats).select {
            UsersChatsJoinTable.chatId.eq(chatId)
                .and(UsersChatsJoinTable.userId.eq(userId))
        }.single().let { row ->
            UserChat(
                row[UsersChatsJoinTable.userId],
                row[UsersChatsJoinTable.chatId],
                Base64Bytes(row[UsersChatsJoinTable.name]),
                Base64Bytes(row[Chats.publicKey]),
            )
        }
    }

    override fun Transactional.getUserChats(userId: Long): List<UserChat> =
        UsersChatsJoinTable.innerJoin(Chats).select {
            UsersChatsJoinTable.userId.eq(userId)
        }.map { row ->
            UserChat(
                row[UsersChatsJoinTable.userId],
                row[UsersChatsJoinTable.chatId],
                Base64Bytes(row[UsersChatsJoinTable.name]),
                Base64Bytes(row[Chats.publicKey]),
            )
        }

    override fun Transactional.deleteUserChat(rqUserId: Long, rqChatId: Long) {
        UsersChatsJoinTable.deleteWhere {
            userId.eq(rqUserId).and(chatId.eq(rqChatId))
        }
    }
}
