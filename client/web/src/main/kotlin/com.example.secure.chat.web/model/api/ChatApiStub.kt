package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.LoginContext
import com.example.secure.chat.web.utils.now
import com.example.secure.chat.web.utils.success
import kotlin.random.Random

@Suppress("unused")
object ChatApiStub : ChatApi {
    override suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean {
        return when (username) {
            "admin", "user" -> true
            else -> false
        }
    }

    override suspend fun loginUser(
        context: LoginContext,
    ): Result<CryptoKeyPair> = with(context) {
        return when (username) {
            "admin", "user" -> Result.success(coder.genRsaKeyPair())
            else -> Result.failure(IllegalArgumentException())
        }
    }

    override suspend fun logoutUser(): Boolean {
        return true
    }

    override suspend fun getLastMessage(
        context: LoginContext,
        chat: Chat.Global,
        key: PrivateCryptoKey,
    ): Result<Pair<Message?, PublicCryptoKey>> {
        val pk = context.genRsaKeyPair()
        return (message.copy(text = "chat-${chat.id}-message-${now()}") to pk.publicKey).success()
    }

    private val random = Random(1234)

    override suspend fun createChat(
        context: LoginContext,
        chatName: String,
        initialMessage: Message,
    ): Result<Pair<Chat.Global, CryptoKeyPair>> {
        return (Chat.Global(random.nextLong(), chatName).apply {
            lastMessage.value = initialMessage
            isLocked.value = false
        } to context.coder.genRsaKeyPair()).let { Result.success(it) }
    }

    override suspend fun leaveChat(context: LoginContext, chat: Chat.Global): Boolean {
        return true
    }

    override suspend fun inviteMember(context: LoginContext, chat: Chat.Global, username: String): Boolean {
        return true
    }

    override suspend fun sendMessage(context: LoginContext, chat: Chat.Global, message: Message): Boolean {
        return true
    }

    override suspend fun acceptInvite(
        context: LoginContext,
        chatName: String,
        invite: Invite,
    ): Result<Pair<Chat.Global, CryptoKeyPair>> {
        return Result.success(Chat.Global(random.nextLong(), chatName) to crypto.genRsaKeyPair())
    }

    override suspend fun subscribeOnNewInvites(context: LoginContext, handler: (List<Invite>) -> Unit) {
        // unsupported
    }

    override suspend fun subscribeOnNewMessages(context: LoginContext, handler: (List<Pair<Long, Message>>) -> Unit) {
        // unsupported
    }

    override suspend fun getChatTimeline(context: LoginContext, chat: Chat.Global): Result<List<Message>> {
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
                initialStatus = MessageStatus.Pending,
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
                initialStatus = MessageStatus.Pending,
                author = Author.Me
            ),
            message.copy(text = "hello 20", timestamp = now()),
            message.copy(text = "hello 21", timestamp = now()),
            message.copy(text = "hello 22", timestamp = now()),
            message.copy(text = "hello 23", timestamp = now()),
            message.copy(text = "hello 24", timestamp = now()),
            message.copy(text = "hello 25", timestamp = now(), initialStatus = MessageStatus.Unread),
        ).let { Result.success(it) }
    }

    override suspend fun getAllChats(context: LoginContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>> {
        val pk = context.coder.genRsaKeyPair().publicKey

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
        ).map { it to pk }.let { Result.success(it) }
    }
}

private val message = Message(
    author = Author.User("Other user"),
    text = "hello 1",
    initialStatus = MessageStatus.Verified,
)
