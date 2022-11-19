package db.tables

import db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable


object UserTables : TableHolder {
    object Users : IntIdTable() {
        val name = varchar("name", 128)
    }
}
