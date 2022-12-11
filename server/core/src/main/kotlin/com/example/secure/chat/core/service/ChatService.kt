package com.example.secure.chat.core.service

import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq

interface ChatService {

    fun getAllChats(userId: Long): List<UserChat>

    fun createChat(rq: UserChatCreateRq, startMessage: MessageCreateRq): Pair<UserChat, Message>

    fun getAllInvites(userId: Long): List<Invite>

    fun sendInvite(rq: InviteCreateRq)

    fun acceptInvite(rq: InviteAcceptRq): UserChat
}
