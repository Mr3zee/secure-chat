package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.domain.db.util.Transactional

interface UserRepository {
    fun Transactional.createUser(rq: UserCreateRq): User
    fun Transactional.getUser(userLogin: String): User?
    fun Transactional.getPublicKey(userId: Long): Base64Bytes?
}
