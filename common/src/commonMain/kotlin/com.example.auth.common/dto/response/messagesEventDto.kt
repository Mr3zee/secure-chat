package com.example.auth.common.dto.response

import com.example.auth.common.dto.model.message.MessageDto
import kotlinx.serialization.Serializable

@Serializable
data class NewMessagesEventDto(
    val messages: List<MessageDto>,
) : ServerEventDto
