package com.example.secure.chat.web.controller.impl.converter

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.auth.common.dto.model.chat.ChatDraftDto
import com.example.auth.common.dto.model.chat.ChatDto
import com.example.auth.common.dto.model.invite.InviteDraftDto
import com.example.auth.common.dto.model.invite.InviteDto
import com.example.secure.chat.base.model.chat.UserChatCreateRq
import com.example.secure.chat.base.model.chat.UserChat
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.user.User

fun toDto(model: UserChat) = ChatDto(
    model.chatId,
    toDto(model.name),
    toDto(model.publicKey),
)

fun toDto(model: Invite) = InviteDto(
    model.chatId,
    toDto(model.encodedKey),
)

fun toModel(user: User, dto: ChatDraftDto) = UserChatCreateRq(
    user,
    toModel(dto.name),
)

fun toModel(dto: InviteDraftDto) = InviteCreateRq(
    dto.userLogin,
    dto.chatId,
    toModel(dto.encodedKey),
)

fun toModel(user: User, invite: InviteDto, chatName: RawBytesDto) = InviteAcceptRq(
    user,
    invite.chatId,
    toModel(chatName),
)
