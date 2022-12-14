package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.domain.db.tables.UserTables.Users
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.UserRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object UserRepositoryImpl : UserRepository {

    override fun Transactional.createUser(rq: UserCreateRq): User =
        Users.insertAndGetId {
            it[login] = rq.login
            it[publicKey] = rq.publicKey.content
        }.let { id -> User(id.value, rq.login, rq.publicKey) }

    override fun Transactional.getUser(userLogin: String): User? =
        Users.select {
            Users.login.eq(userLogin)
        }.singleOrNull()?.let { row ->
            User(
                row[Users.id].value,
                row[Users.login],
                Base64Bytes(row[Users.publicKey]),
            )
        }

    override fun Transactional.getPublicKey(userId: Long): Base64Bytes? =
        Users.slice(Users.publicKey).select {
            Users.id.eq(userId)
        }.singleOrNull()?.let { row ->
            row[Users.publicKey]
        }?.let(::Base64Bytes)
}
