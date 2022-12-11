package com.example.secure.chat.core.service.impl

import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.domain.db.util.tx
import com.example.secure.chat.domain.repository.ChatRepository
import com.example.secure.chat.domain.repository.InviteRepository
import com.example.secure.chat.domain.repository.MessageRepository
import com.example.secure.chat.domain.repository.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatServiceImpl : ChatService, KoinComponent {

    private val chatRepository by inject<ChatRepository>()
    private val inviteRepository by inject<InviteRepository>()
    private val messageRepository by inject<MessageRepository>()
    private val userRepository by inject<UserRepository>()

    override suspend fun getAllChats(userId: Long): List<UserChat> = tx {
        with(chatRepository) {
            getUsersChats(userId)
        }
    }

    override suspend fun createChat(
        rq: UserChatCreateRq,
        startMessage: ByteArrayWrapper,
    ): Pair<UserChat, Message> = tx {
        val userChat: UserChat
        with(chatRepository) {
            val chatId = createChat()
            userChat = createUserChat(chatId, rq)
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
        val user = with(userRepository) { getUser(rq.userLogin) }
            ?: throw NoSuchElementException("Failed to find user with login = ${rq.userLogin}")
        with(inviteRepository) {
            createInvite(
                Invite(user.id, rq.chatId, rq.encodedKey)
            )
        }
    }

    override suspend fun acceptInvite(rq: InviteAcceptRq): UserChat = tx {
        with(inviteRepository) {
            deleteInvite(rq)
        }
        with(chatRepository) {
            createUserChat(rq.chatId, UserChatCreateRq(rq.user, rq.chatName))
        }
    }
}
