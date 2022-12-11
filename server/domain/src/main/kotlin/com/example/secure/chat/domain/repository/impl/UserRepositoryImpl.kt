package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.tables.UserTables.Users
import com.example.secure.chat.domain.repository.UserRepository
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object UserRepositoryImpl : UserRepository {

    override fun Transaction.createUser(rq: UserCreateRq) {
        Users.insert {
            it[login] = rq.login
            it[publicKey] = rq.publicKey.byteArray
        }
    }

    override fun Transaction.getPublicKey(userId: Long): ByteArrayWrapper? =
        Users.slice(Users.publicKey).select {
            Users.id.eq(userId)
        }.singleOrNull()?.let { row ->
            row[Users.publicKey]
        }?.let(::ByteArrayWrapper)
}
