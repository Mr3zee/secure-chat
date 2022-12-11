package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.domain.db.util.Transactional

interface ChatRepository {
    fun Transactional.createChat(): Long
    fun Transactional.createUserChat(rqChatId: Long, rq: UserChatCreateRq): UserChat
    fun Transactional.getUsersChats(userId: Long): List<UserChat>
}
