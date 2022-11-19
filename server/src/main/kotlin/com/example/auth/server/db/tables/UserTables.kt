package com.example.auth.server.db.tables

import com.example.auth.server.db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable

object UserTables : TableHolder {
    object Users : IntIdTable() {
        val name = varchar("name", 128)
    }
}
