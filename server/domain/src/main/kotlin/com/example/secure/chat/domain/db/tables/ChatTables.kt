package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.UserTables.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object ChatTables : TableHolder {

    object Chats : LongIdTable("chats") {
        val publicKey = binary("public_key")
    }

    object Invites : Table("chat_invites") {
        val userId = long("user_id").references(Users.id)
        val chatId = long("chat_id").references(Chats.id)
        val encodedKey = binary("encoded_key")
        override val primaryKey = PrimaryKey(userId, chatId)
    }
}
