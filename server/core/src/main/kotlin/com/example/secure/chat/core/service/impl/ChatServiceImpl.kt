package com.example.secure.chat.core.service.impl

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.domain.db.util.tx
import com.example.secure.chat.domain.repository.ChatRepository
import com.example.secure.chat.domain.repository.InviteRepository
import com.example.secure.chat.domain.repository.MessageRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ChatServiceImpl : ChatService, KoinComponent {

    private val chatRepository by inject<ChatRepository>()
    private val inviteRepository by inject<InviteRepository>()
    private val messageRepository by inject<MessageRepository>()

    override suspend fun getAllChats(userId: Long): List<UserChat> = tx {
        with(chatRepository) {
            getUserChats(userId)
        }
    }

    override suspend fun createChat(
        rq: UserChatCreateRq,
        publicKey: Base64Bytes,
        startMessage: Base64Bytes,
    ): Pair<UserChat, Message> = tx {
        val userChat: UserChat = with(chatRepository) {
            val chatId = createChat(publicKey)
            createUserChat(chatId, rq)
            getUserChat(chatId)
        }
        userChat to with(messageRepository) {
            val messageId = createMessage(
                MessageCreateRq(rq.user, userChat.chatId, startMessage)
            )
            getMessage(messageId)
        }
    }

    override suspend fun getAllInvites(userId: Long): List<Invite> = tx {
        with(inviteRepository) {
            getUserInvites(userId)
        }
    }

    override suspend fun sendInvite(rq: InviteCreateRq) = tx {
        with(inviteRepository) {
            createInvite(
                InviteCreateRq(rq.userId, rq.chatId, rq.encodedKey)
            )
        }
    }

    override suspend fun acceptInvite(rq: InviteAcceptRq): UserChat = tx {
        with(inviteRepository) {
            deleteInvite(rq)
        }
        with(chatRepository) {
            createUserChat(rq.chatId, UserChatCreateRq(rq.user, rq.chatName))
            getUserChat(rq.chatId)
        }
    }

    override suspend fun getChatById(id: Long): UserChat = tx {
        with(chatRepository) {
            getUserChat(id)
        }
    }

    override suspend fun leaveChat(userId: Long, chatId: Long) = tx {
        with(chatRepository) {
            deleteUserChat(userId, chatId)
        }
    }
}
