package com.example.secure.chat.domain.db.connection

import com.example.secure.chat.domain.db.util.tx
import org.jetbrains.exposed.sql.*
import org.koin.core.component.KoinComponent

// marker interface
interface TableHolder

abstract class DbConnection : KoinComponent {

    protected abstract val dbUrl: String
    protected abstract val dbUsername: String
    protected abstract val dbPassword: String
    protected abstract val driverUrl: String

    private val db by lazy {
        Database.connect(dbUrl, driverUrl, user = dbUsername, password = dbPassword)
    }

    private val tables by lazy {
        val tableHolders = getKoin().getAll<TableHolder>()
        tableHolders.flatMap {
            it::class.nestedClasses.map { clazz -> clazz.objectInstance }.filterIsInstance<Table>()
        }
    }

    suspend fun init(isDebug: Boolean = false) {
        tx(db) {
            if (isDebug) {
                transaction.addLogger(Slf4jSqlDebugLogger)
            }

            SchemaUtils.create(*tables.toTypedArray(), inBatch = true)
        }
    }
}
