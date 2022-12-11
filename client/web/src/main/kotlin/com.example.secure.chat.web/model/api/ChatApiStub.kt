package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.chat.Author
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.MessageStatus
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.utils.now
import kotlin.random.Random

object ChatApiStub : ChatApi {
    override suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean {
        return when (username) {
            "admin", "user" -> true
            else -> false
        }
    }

    override suspend fun loginUser(
        username: String,
        privateCryptoKey: PrivateCryptoKey,
        coder: Coder,
    ): Result<CryptoKeyPair> {
        return when (username) {
            "admin", "user" -> Result.success(coder.genRsaKeyPair())
            else -> Result.failure(IllegalArgumentException())
        }
    }

    override suspend fun getLastMessage(chat: Chat.Global, key: PrivateCryptoKey, coder: Coder): Message {
        return message.copy(text = "chat-${chat.id}-message-${now()}")
    }

    private val random = Random(1234)

    override suspend fun createChat(
        chatName: String,
        initialMessage: Message,
        coder: Coder,
    ): Pair<Chat.Global, CryptoKeyPair> {
        return Chat.Global(random.nextLong(), chatName).apply {
            lastMessage.value = initialMessage
            isLocked.value = false
        } to coder.genRsaKeyPair()
    }

    override suspend fun getChatTimeline(chat: Chat.Global): List<Message> {
        return listOf(
            message.copy(
                text = "hello 1".repeat(100),
                timestamp = now(),
                initialStatus = MessageStatus.Failed,
                author = Author.Me
            ),
            message.copy(text = "hello 2", timestamp = now()),
            message.copy(text = "hello 3", timestamp = now()),
            message.copy(text = "hello 4", timestamp = now()),
            message.copy(text = "hello 5", timestamp = now()),
            message.copy(
                text = "hello 5",
                timestamp = now(),
                initialStatus = MessageStatus.Failed,
                author = Author.Me
            ),
            message.copy(text = "hello 6", timestamp = now()),
            message.copy(text = "hello 7", timestamp = now()),
            message.copy(text = "hello 8", timestamp = now()),
            message.copy(text = "hello 9", timestamp = now()),
            message.copy(
                text = "hello 10",
                timestamp = now(),
                initialStatus = MessageStatus.Local,
                author = Author.Me
            ),
            message.copy(text = "hello 11", timestamp = now()),
            message.copy(text = "hello 12", timestamp = now()),
            message.copy(text = "hello 13", timestamp = now()),
            message.copy(text = "hello 14", timestamp = now()),
            message.copy(
                text = "hello 15",
                timestamp = now(),
                initialStatus = MessageStatus.Failed,
                author = Author.Me
            ),
            message.copy(text = "hello16".repeat(100), timestamp = now()),
            message.copy(text = "hello 17", timestamp = now()),
            message.copy(text = "hello 18", timestamp = now()),
            message.copy(
                text = "hello 19",
                timestamp = now(),
                initialStatus = MessageStatus.Local,
                author = Author.Me
            ),
            message.copy(text = "hello 20", timestamp = now()),
            message.copy(text = "hello 21", timestamp = now()),
            message.copy(text = "hello 22", timestamp = now()),
            message.copy(text = "hello 23", timestamp = now()),
            message.copy(text = "hello 24", timestamp = now()),
            message.copy(text = "hello 25", timestamp = now(), initialStatus = MessageStatus.Unread),
        )
    }

    override suspend fun getAllChats(coder: Coder): List<Pair<Chat.Global, PublicCryptoKey>> {
        val pk = coder.genRsaKeyPair().publicKey

        return listOf(
            Chat.Global(0, "Chat 1"),
            Chat.Global(1, "Chat 2"),
            Chat.Global(2, "Chat 3"),
            Chat.Global(3, "Chat 4"),
            Chat.Global(4, "Chat 5"),
            Chat.Global(5, "Chat 6"),
            Chat.Global(6, "Chat 7").apply { isLocked.value = true },
            Chat.Global(7, "Chat 8"),
            Chat.Global(8, "Chat 9"),
            Chat.Global(9, "Chat 10"),
            Chat.Global(10, "Chat 11"),
            Chat.Global(11, "Chat 12").apply { isLocked.value = true },
            Chat.Global(12, "Chat 13").apply {
                lastMessage.value = message.copy(
                    text = "hello 25",
                    initialStatus = MessageStatus.Unread
                )
            },
        ).map { it to pk }
    }
}

private val message = Message(
    author = Author.User("Other user"),
    text = "hello 1",
    initialStatus = MessageStatus.Verified,
)
