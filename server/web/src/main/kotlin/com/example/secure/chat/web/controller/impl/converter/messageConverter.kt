package com.example.secure.chat.web.controller.impl.converter

import com.example.auth.common.dto.model.message.MessageDraftDto
import com.example.auth.common.dto.model.message.MessageDto
import com.example.secure.chat.base.model.message.Message
import com.example.secure.chat.base.model.message.MessageCreateRq
import com.example.secure.chat.base.model.user.User

fun toDto(model: Message) = MessageDto(
    model.id,
    model.chatId,
    model.userLogin,
    toDto(model.text),
    model.createdTs,
)

fun toModel(user: User, dto: MessageDraftDto) = MessageCreateRq(
    user,
    dto.chatId,
    toModel(dto.encodedText),
)
