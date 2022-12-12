package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import com.example.secure.chat.domain.db.tables.UserTables.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MessageTables : TableHolder {

    object Messages : LongIdTable("messages") {
        val chatId = long("chat_id").index().references(Chats.id)
        val userId = long("user_id").index().references(Users.id)
        val text = varchar("text", 8192 - 2)
        val createdTs = timestamp("created_ts").defaultExpression(CurrentTimestamp())
    }
}
