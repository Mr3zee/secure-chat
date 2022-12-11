package com.example.secure.chat.web.controller

import com.example.auth.common.dto.request.GetUserPublicKeyRequestDto
import com.example.auth.common.dto.response.GetUserPublicKeyResponseDto
import com.example.secure.chat.base.model.user.User
import com.example.secure.chat.base.model.user.UserCreateRq

interface UserController  {

    suspend fun getUserPublicKey(
        rq: GetUserPublicKeyRequestDto,
    ): GetUserPublicKeyResponseDto

    suspend fun getUser(login: String): User?

    suspend fun register(createRq: UserCreateRq): User
}
