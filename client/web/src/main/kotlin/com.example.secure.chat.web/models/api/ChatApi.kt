package com.example.secure.chat.web.models.api

import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message

interface ChatApi {
    suspend fun getChatTimeline(chat: Chat): List<Message>

    suspend fun getAllChats(): List<Chat>
}
