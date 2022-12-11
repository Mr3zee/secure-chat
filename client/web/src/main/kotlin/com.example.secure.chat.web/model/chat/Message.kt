package com.example.secure.chat.web.model.chat

import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.utils.now
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random

data class Message(
    val author: Author,
    val text: String,
    val id: Long = Random(42).nextLong(),
    val timestamp: LocalDateTime = now(),
    private val initialStatus: MessageStatus = MessageStatus.Local,
) {
    val status: MutableProperty<MessageStatus> = mutableProperty(initialStatus)
}

enum class MessageStatus {
    Local, Verified, Failed, Unread
}
