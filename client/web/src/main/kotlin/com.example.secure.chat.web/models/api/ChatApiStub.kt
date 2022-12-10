package com.example.secure.chat.web.models.api

import androidx.compose.runtime.mutableStateOf
import com.example.auth.common.chat.ChatDto
import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.models.chat.MessageStatus
import com.example.secure.chat.web.utils.now

object ChatApiStub : ChatApi {
    override suspend fun getChatTimeline(chat: Chat): List<Message> {
        return when {
            chat is Chat.Global && chat.dto.id == 12L -> {
                listOf(
                    message.copy(
                        text = "hello 1".repeat(100),
                        timestamp = now(),
                        status = mutableStateOf(MessageStatus.Failed),
                        author = Author.Me
                    ),
                    message.copy(text = "hello 2", timestamp = now()),
                    message.copy(text = "hello 3", timestamp = now()),
                    message.copy(text = "hello 4", timestamp = now()),
                    message.copy(text = "hello 5", timestamp = now()),
                    message.copy(
                        text = "hello 5",
                        timestamp = now(),
                        status = mutableStateOf(MessageStatus.Failed),
                        author = Author.Me
                    ),
                    message.copy(text = "hello 6", timestamp = now()),
                    message.copy(text = "hello 7", timestamp = now()),
                    message.copy(text = "hello 8", timestamp = now()),
                    message.copy(text = "hello 9", timestamp = now()),
                    message.copy(
                        text = "hello 10",
                        timestamp = now(),
                        status = mutableStateOf(MessageStatus.Local),
                        author = Author.Me
                    ),
                    message.copy(text = "hello 11", timestamp = now()),
                    message.copy(text = "hello 12", timestamp = now()),
                    message.copy(text = "hello 13", timestamp = now()),
                    message.copy(text = "hello 14", timestamp = now()),
                    message.copy(
                        text = "hello 15",
                        timestamp = now(),
                        status = mutableStateOf(MessageStatus.Failed),
                        author = Author.Me
                    ),
                    message.copy(text = "hello16".repeat(100), timestamp = now()),
                    message.copy(text = "hello 17", timestamp = now()),
                    message.copy(text = "hello 18", timestamp = now()),
                    message.copy(
                        text = "hello 19",
                        timestamp = now(),
                        status = mutableStateOf(MessageStatus.Local),
                        author = Author.Me
                    ),
                    message.copy(text = "hello 20", timestamp = now()),
                    message.copy(text = "hello 21", timestamp = now()),
                    message.copy(text = "hello 22", timestamp = now()),
                    message.copy(text = "hello 23", timestamp = now()),
                    message.copy(text = "hello 24", timestamp = now()),
                    message.copy(text = "hello 25", timestamp = now(), status = mutableStateOf(MessageStatus.Unread)),
                )
            }

            else -> emptyList()
        }
    }

    override suspend fun getAllChats(): List<Chat> {
        return listOf(
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 1")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(1, "Chat 2")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(2, "Chat 3")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(3, "Chat 4")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(4, "Chat 5")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(5, "Chat 6")),
            Chat.Global(mutableStateOf(true), mutableStateOf(null), ChatDto(6, "Chat 7")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(7, "Chat 8")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(8, "Chat 9")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(9, "Chat 10")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(10, "Chat 11")),
            Chat.Global(mutableStateOf(true), mutableStateOf(null), ChatDto(11, "Chat 12")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(12, "Chat 13")).apply {
                lastMessage.value = message.copy(text = "hello 25", status = mutableStateOf(MessageStatus.Unread))
            },
        )
    }
}

private val message = Message(
    author = Author.User("Other user"),
    id = 0,
    timestamp = now(),
    text = "hello 1",
    status = mutableStateOf(MessageStatus.Verified),
)
