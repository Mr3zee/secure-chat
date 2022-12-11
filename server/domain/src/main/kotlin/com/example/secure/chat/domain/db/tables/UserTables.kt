package com.example.secure.chat.domain.db.tables

import com.example.secure.chat.domain.db.connection.TableHolder
import org.jetbrains.exposed.dao.id.LongIdTable

object UserTables : TableHolder {

    object Users : LongIdTable("users") {
        val login = varchar("login", 128 - 1).uniqueIndex()
        val publicKey = binary("public_key")
    }
}
