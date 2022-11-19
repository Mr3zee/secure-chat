package services

import User
import db.tables.UserTables
import db.util.tx
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll


class UserService {
    suspend fun addUser(user: User): List<User> {
        return tx {
            UserTables.Users.insert {
                it[name] = user.name
            }

            allUsers()
        }
    }

    private fun allUsers(): List<User> {
        return UserTables.Users.selectAll().map {
            User(it[UserTables.Users.name])
        }
    }

    suspend fun getAllUsers() = tx { allUsers() }
}
