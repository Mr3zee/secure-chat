package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Invite
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.ApiContext

interface ChatApi {
    suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean

    suspend fun loginUser(context: ApiContext): Result<CryptoKeyPair>

    suspend fun logoutUser(): Boolean

    suspend fun getLastMessage(context: ApiContext, chat: Chat.Global, key: PrivateCryptoKey): Result<Pair<Message?, PublicCryptoKey>>

    suspend fun createChat(
        context: ApiContext,
        chatName: String,
        initialMessage: Message,
    ): Result<Pair<Chat.Global, CryptoKeyPair>>

    suspend fun getAllChats(context: ApiContext): Result<List<Pair<Chat.Global, PublicCryptoKey>>>

    suspend fun getChatTimeline(context: ApiContext, chat: Chat.Global): Result<List<Message>>

    suspend fun leaveChat(context: ApiContext, chat: Chat.Global): Boolean

    suspend fun sendMessage(context: ApiContext, chat: Chat.Global, message: Message): Boolean

    suspend fun listInvites(context: ApiContext): Result<List<Invite>>

    suspend fun inviteMember(context: ApiContext, chat: Chat.Global, username: String): Boolean

    suspend fun acceptInvite(
        context: ApiContext,
        chatName: String,
        invite: Invite,
    ): Result<Pair<Chat.Global, CryptoKeyPair>>

    fun subscribeOnNewInvites(context: ApiContext, handler: (List<Invite>) -> Unit)

    fun subscribeOnNewMessages(context: ApiContext, handler: (List<Pair<Long, Message>>) -> Unit)
}
