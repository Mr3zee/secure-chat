package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.chat.UserChat
import org.jetbrains.exposed.sql.Transaction

interface ChatRepository {
    fun Transaction.createChat(): Long
    fun Transaction.createUserChat(rqChatId: Long, rq: UserChatCreateRq): UserChat
    fun Transaction.getUsersChats(userId: Long): List<UserChat>
}
