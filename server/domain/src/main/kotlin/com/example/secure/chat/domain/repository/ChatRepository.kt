package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.chat.ChatCreateRq
import com.example.secure.chat.base.model.chat.UsersChat
import org.jetbrains.exposed.sql.Transaction

interface ChatRepository {
    fun Transaction.createChat(rq: ChatCreateRq)
    fun Transaction.getUsersChats(userId: Long): List<UsersChat>
}
