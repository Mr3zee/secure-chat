package com.example.auth.server.db.connection

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import com.example.auth.server.property

fun Application.postgres(isDebug: Boolean = false) = runBlocking {
    PostgresDbConnection(this@postgres).init(isDebug)
}

class PostgresDbConnection(application: Application) : DbConnection() {
    override val dbUrl: String
    override val dbUsername: String
    override val dbPassword: String

    init {
        val host: String = application.property("com.example.auth.server.db.postgres.host").getString()
        val port: Int = application.property("com.example.auth.server.db.postgres.port").getString().toInt()
        val name: String = application.property("com.example.auth.server.db.postgres.name").getString()

        dbUrl = "jdbc:postgresql://$host:$port/$name"
        dbUsername = application.property("com.example.auth.server.db.postgres.username").getString()
        dbPassword = application.property("com.example.auth.server.db.postgres.password").getString()
    }

    override val driverUrl = "org.postgresql.Driver"
}
