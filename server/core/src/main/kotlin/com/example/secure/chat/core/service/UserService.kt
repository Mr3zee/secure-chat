package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq

interface UserService {

    fun register(rq: UserCreateRq): User

    fun load(login: String): User
}
