package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.creds.Coder

interface ChatApi {
    suspend fun registerUser(username: String, publicCryptoKey: PublicCryptoKey, coder: Coder): Boolean

    suspend fun loginUser(username: String, privateCryptoKey: PrivateCryptoKey, coder: Coder): Boolean

    suspend fun getLastMessage(chat: Chat.Global, key: PrivateCryptoKey, coder: Coder): Message?

    suspend fun getAllChats(): List<Chat.Global>

    suspend fun getChatTimeline(chat: Chat): List<Message>
}
