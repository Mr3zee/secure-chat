package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq

interface UserService {

    suspend fun register(rq: UserCreateRq): User

    suspend fun load(login: String): User?
}
