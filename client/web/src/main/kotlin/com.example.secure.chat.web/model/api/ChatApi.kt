package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Invite
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.LoginContext

interface ChatApi {
    suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean

    suspend fun loginUser(context: LoginContext): Result<CryptoKeyPair>

    suspend fun getLastMessage(context: LoginContext, chat: Chat.Global, key: PrivateCryptoKey): Result<Message?>

    suspend fun createChat(
        context: LoginContext,
        chatName: String,
        initialMessage: Message,
    ): Result<Pair<Chat.Global, CryptoKeyPair>>

    suspend fun getAllChats(context: LoginContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>>

    suspend fun getChatTimeline(context: LoginContext, chat: Chat.Global): Result<List<Message>>

    suspend fun leaveChat(context: LoginContext, chat: Chat.Global): Boolean

    suspend fun inviteMember(context: LoginContext, chat: Chat.Global, username: String): Boolean

    suspend fun sendMessage(context: LoginContext, chat: Chat.Global, message: Message): Boolean

    suspend fun acceptInvite(
        context: LoginContext,
        chatName: String,
        invite: Invite,
    ): Result<Pair<Chat.Global, CryptoKeyPair>>

    suspend fun subscribeOnNewInvites(context: LoginContext, handler: (List<Invite>) -> Unit)

    suspend fun subscribeOnNewMessages(context: LoginContext, handler: (List<Pair<Long, Message>>) -> Unit)
}
