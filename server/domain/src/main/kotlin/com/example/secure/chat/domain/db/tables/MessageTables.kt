package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import com.example.secure.chat.domain.db.tables.ChatTables.Chats
import org.jetbrains.exposed.dao.id.LongIdTable

object MessageTables : TableHolder {

    object Messages : LongIdTable("messages") {
        val chatId = long("chat_id").index().references(Chats.id)
        val text = binary("text")
    }
}
