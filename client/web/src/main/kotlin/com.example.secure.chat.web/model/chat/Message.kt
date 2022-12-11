package com.example.secure.chat.web.model.chat

import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.utils.now
import kotlinx.datetime.LocalDateTime

data class Message(
    val author: Author,
    val text: String,
    val id: Long? = null,
    val timestamp: LocalDateTime = now(),
    val isSecret: Boolean = false,
    private val initialStatus: MessageStatus = MessageStatus.Pending,
) {
    val status: MutableProperty<MessageStatus> = mutableProperty(initialStatus)
}

enum class MessageStatus {
    Pending, Verified, Failed, Unread, Local
}
