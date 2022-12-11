package com.example.secure.chat.web.controller.impl

import com.example.auth.common.dto.request.GetUserPublicKeyRequestDto
import com.example.auth.common.dto.response.GetUserPublicKeyResponseDto
import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq
import com.example.secure.chat.core.service.UserService
import com.example.secure.chat.web.controller.UserController
import com.example.secure.chat.web.controller.impl.converter.toDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserControllerImpl : UserController, KoinComponent {

    private val userService by inject<UserService>()

    override suspend fun getUserPublicKey(rq: GetUserPublicKeyRequestDto): GetUserPublicKeyResponseDto {
        val user = userService.load(rq.userLogin)
        return GetUserPublicKeyResponseDto(
            rq.requestId,
            user?.publicKey?.let(::toDto),
        )
    }

    override suspend fun getUser(login: String): User? {
        return userService.load(login)
    }

    override suspend fun register(createRq: UserCreateRq): User {
        return userService.register(createRq)
    }
}
