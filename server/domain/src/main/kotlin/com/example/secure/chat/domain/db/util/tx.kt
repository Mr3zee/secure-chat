package com.example.secure.chat.domain.db.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> tx(
    db: Database? = null,
    transactionIsolation: Int? = null,
    block: suspend Transactional.() -> T,
): T {
    return newSuspendedTransaction(Dispatchers.IO, db, transactionIsolation) {
        with(Transactional(this)) {
            block()
        }
    }
}
