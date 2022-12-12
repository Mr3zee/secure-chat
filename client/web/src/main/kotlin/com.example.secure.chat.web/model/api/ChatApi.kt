package com.example.secure.chat.web.model.api

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Invite
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.coder.Coder

interface ChatApi {
    suspend fun registerUser(username: String, keyPair: CryptoKeyPair, coder: Coder): Boolean

    suspend fun loginUser(username: String, privateCryptoKey: PrivateCryptoKey, coder: Coder): Result<CryptoKeyPair>

    suspend fun getLastMessage(chat: Chat.Global, key: PrivateCryptoKey, coder: Coder): Message?

    suspend fun createChat(chatName: String, initialMessage: Message, coder: Coder): Pair<Chat.Global, CryptoKeyPair>

    suspend fun getAllChats(coder: Coder): List<Pair<Chat.Global, PublicCryptoKey>>

    suspend fun getChatTimeline(chat: Chat.Global): List<Message>

    suspend fun leaveChat(chat: Chat.Global): Boolean

    suspend fun inviteMember(chat: Chat.Global, username: String): Boolean

    suspend fun sendMessage(chat: Chat.Global, message: Message): Boolean

    suspend fun acceptInvite(chatName: String, invite: Invite): Result<Pair<Chat.Global, CryptoKeyPair>>

    suspend fun subscribeOnNewInvites(handler: (List<Invite>) -> Unit)

    suspend fun subscribeOnNewMessages(handler: (List<Pair<Long, Message>>) -> Unit)
}
