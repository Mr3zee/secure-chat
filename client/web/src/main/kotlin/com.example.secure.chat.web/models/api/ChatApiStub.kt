package com.example.secure.chat.web.models.api

import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message

object ChatApiStub : ChatApi {
    override suspend fun getChatTimeline(chat: Chat): List<Message> {
        return emptyList()
    }
}
