package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import org.jetbrains.exposed.sql.Transaction

interface UserRepository {
    fun Transaction.createUser(rq: UserCreateRq)
    fun Transaction.getPublicKey(userId: Long): ByteArrayWrapper?
}
