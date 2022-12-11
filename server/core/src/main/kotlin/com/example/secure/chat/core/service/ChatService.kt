package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

interface ChatService {

    suspend fun getAllChats(userId: Long): List<UserChat>

    suspend fun createChat(rq: UserChatCreateRq, startMessage: ByteArrayWrapper): Pair<UserChat, Message>

    suspend fun getAllInvites(userId: Long): List<Invite>

    suspend fun sendInvite(rq: InviteCreateRq)

    suspend fun acceptInvite(rq: InviteAcceptRq): UserChat
}
