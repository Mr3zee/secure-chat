package com.example.secure.chat.web.controller.impl

import com.example.auth.common.dto.event.NewMessagesEventDto
import com.example.auth.common.dto.request.*
import com.example.auth.common.dto.response.*
import com.example.secure.chat.core.registry.NewMessagesRegistry
import com.example.secure.chat.core.service.ChatService
import com.example.secure.chat.core.service.UserService
import com.example.secure.chat.web.controller.ChatController
import com.example.secure.chat.web.controller.impl.converter.toDto
import com.example.secure.chat.web.controller.impl.converter.toModel
import com.example.secure.chat.web.routing.websocket.WebSocketSessionContext
import io.ktor.server.websocket.*
import kotlinx.coroutines.isActive
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ChatControllerImpl : ChatController, KoinComponent {

    private val logger = KotlinLogging.logger { }

    private val chatService by inject<ChatService>()
    private val userService by inject<UserService>()
    private val newMessagesRegistry by inject<NewMessagesRegistry>()

    override suspend fun chatList(
        context: WebSocketSessionContext,
        rq: ChatListRequestDto,
    ): ChatListResponseDto {
        return ChatListResponseDto(
            rq.requestId,
            chatService.getAllChats(
                context.currentUser.id,
            ).map(::toDto)
        )
    }

    override suspend fun chatCreate(
        context: WebSocketSessionContext,
        rq: ChatCreateRequestDto,
    ): ChatCreateResponseDto {
        val (chat, message) = chatService.createChat(
            toModel(context.currentUser, rq.chat),
            toModel(rq.chat.publicKey),
            toModel(rq.startMessageText),
        )
        return ChatCreateResponseDto(
            rq.requestId,
            toDto(chat),
            toDto(message),
        )
    }

    override suspend fun chatLeave(
        context: WebSocketSessionContext,
        rq: ChatLeaveRequestDto,
    ): ChatLeaveResponseDto {
        return chatService.leaveChat(
            context.currentUser.id,
            rq.chatId,
        ).let { ChatLeaveResponseDto(rq.requestId) }
    }

    override suspend fun chatSubscribe(
        context: WebSocketSessionContext,
        rq: ChatSubscribeRequestDto,
    ): ChatSubscribeResponseDto {
        return newMessagesRegistry.subscribe(
            rq.chatId,
            context.sessionId,
        ) { messages ->
            try {
                if (!context.isActive) {
                    return@subscribe false
                }
                val event = NewMessagesEventDto(messages.map(::toDto))
                context.sendSerialized(event)
                logger.debug { "Sent ${messages.size} events to session ${context.sessionId}" }
            } catch (e: Exception) {
                logger.error(e) {
                    "Failed to send message to session"
                }
            }
            true
        }.let { ChatSubscribeResponseDto(rq.requestId) }
    }

    override suspend fun inviteList(
        context: WebSocketSessionContext,
        rq: InviteListRequestDto,
    ): InviteListResponseDto {
        return InviteListResponseDto(
            rq.requestId,
            chatService.getAllInvites(
                context.currentUser.id,
            ).map(::toDto),
        )
    }

    override suspend fun inviteSend(
        context: WebSocketSessionContext,
        rq: InviteSendRequestDto,
    ): InviteSendResponseDto {
        val invitedUser = userService.load(rq.userLogin)
            ?: throw NoSuchElementException("Failed to find user with login = ${rq.userLogin}")
        return chatService.sendInvite(
            toModel(invitedUser, rq.invite),
        ).let { InviteSendResponseDto(rq.requestId) }
    }

    override suspend fun inviteAccept(
        context: WebSocketSessionContext,
        rq: InviteAcceptRequestDto,
    ): InviteAcceptResponseDto {
        return InviteAcceptResponseDto(
            rq.requestId,
            chatService.acceptInvite(
                toModel(context.currentUser, rq.invite, rq.chatName),
            ).let(::toDto),
        )
    }
}
