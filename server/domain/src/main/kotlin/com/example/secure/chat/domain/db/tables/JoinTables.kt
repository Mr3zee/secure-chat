package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.UserTables.Users
import org.jetbrains.exposed.sql.Table

object JoinTables : TableHolder {

    object UsersChatsJoinTable : Table("users_chats_join_table") {
        val userId = long("user_id").index().references(Users.id)
        val chatId = long("chat_id").index().references(Chats.id)
        val name = binary("chat_name", 2048 - 2)
        override val primaryKey: PrimaryKey = PrimaryKey(userId, chatId)

        init {
            uniqueIndex(userId, name)
        }
    }
}
