package com.example.secure.chat.web.models.api

import androidx.compose.runtime.mutableStateOf
import com.example.auth.common.chat.ChatDto
import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.models.chat.MessageStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ChatApiStub : ChatApi {
    override suspend fun getChatTimeline(chat: Chat): List<Message> {
        return emptyList()
    }

    override suspend fun getAllChats(): List<Chat> {
        return listOf(
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 1")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(1, "Chat 2")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(2, "Chat 3")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(3, "Chat 4")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(4, "Chat 5")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(5, "Chat 6")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(6, "Chat 7")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(7, "Chat 8")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(8, "Chat 9")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(9, "Chat 10")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(10, "Chat 11")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(11, "Chat 12")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(12, "Chat 13")).apply {
                lastMessage.value = Message.Text(
                    Author.Me,
                    0,
                    Clock.System.now().toLocalDateTime(TimeZone.UTC),
                    "hello text doihwiuwhiuxbwievbiwbxnewhew dhwidghiegdwigdwuiygeidgewidgewi",
                    mutableStateOf(MessageStatus.Verified),
                )
            },
        )
    }
}
