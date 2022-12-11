package com.example.secure.chat.web.model.chat

import com.example.secure.chat.web.crypto.PublicCryptoKey

data class Invite(
    val chatId: Long,
    val publicCryptoKey: PublicCryptoKey,
)
