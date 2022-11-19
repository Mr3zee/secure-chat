package db.connection

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import property


fun Application.postgres(isDebug: Boolean = false) = runBlocking {
    PostgresDbConnection(this@postgres).init(isDebug)
}

class PostgresDbConnection(application: Application) : DbConnection() {
    override val dbUrl: String
    override val dbUsername: String
    override val dbPassword: String

    init {
        val host: String = application.property("db.postgres.host").getString()
        val port: Int = application.property("db.postgres.port").getString().toInt()
        val name: String = application.property("db.postgres.name").getString()

        dbUrl = "jdbc:postgresql://$host:$port/$name"
        dbUsername = application.property("db.postgres.username").getString()
        dbPassword = application.property("db.postgres.password").getString()
    }

    override val driverUrl = "org.postgresql.Driver"
}
