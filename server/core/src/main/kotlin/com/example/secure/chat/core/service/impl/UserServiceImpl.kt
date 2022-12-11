package com.example.secure.chat.core.service.impl

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.core.service.UserService
import com.example.secure.chat.domain.db.util.tx
import com.example.secure.chat.domain.repository.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserServiceImpl : UserService, KoinComponent {

    private val userRepository by inject<UserRepository>()

    override suspend fun register(rq: UserCreateRq): User = tx {
        with(userRepository) {
            createUser(rq)
        }
    }

    override suspend fun load(login: String): User? = tx {
        with (userRepository) {
            getUser(login)
        }
    }
}
