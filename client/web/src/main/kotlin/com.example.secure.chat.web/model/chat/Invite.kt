package com.example.secure.chat.web.model.chat

import com.example.auth.common.dto.model.byte.Base64BytesDto

data class Invite(
    val chatId: Long,
    val encodedKey: Base64BytesDto,
)
