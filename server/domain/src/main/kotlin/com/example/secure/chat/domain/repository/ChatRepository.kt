package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.util.Transactional

interface ChatRepository {
    fun Transactional.createChat(rqPublicKey: ByteArrayWrapper): Long
    fun Transactional.createUserChat(rqChatId: Long, rq: UserChatCreateRq)
    fun Transactional.getUserChat(chatId: Long): UserChat
    fun Transactional.getUserChats(userId: Long): List<UserChat>
    fun Transactional.deleteUserChat(rqUserId: Long, rqChatId: Long)
}
