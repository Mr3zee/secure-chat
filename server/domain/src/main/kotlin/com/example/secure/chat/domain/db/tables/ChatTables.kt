package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.UserTables.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ChatTables : TableHolder {

    object Chats : LongIdTable("chats") {
        val publicKey = varchar("public_key", 8192 - 2)
    }

    object Invites : LongIdTable("chat_invites") {
        val userId = long("user_id").references(Users.id)
        val chatId = long("chat_id").references(Chats.id)
        val encodedKey = varchar("encoded_key", 8192 - 2)
        val createdTs = timestamp("created_ts").defaultExpression(CurrentTimestamp())

        init {
            uniqueIndex(userId, chatId)
        }
    }
}
