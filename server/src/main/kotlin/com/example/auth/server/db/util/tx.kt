package com.example.auth.server.db.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> tx(db: Database? = null, transactionIsolation: Int? = null, block: suspend Transaction.() -> T): T {
    return newSuspendedTransaction(Dispatchers.IO, db, transactionIsolation) { block() }
}
