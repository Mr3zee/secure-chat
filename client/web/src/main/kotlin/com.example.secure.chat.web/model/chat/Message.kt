package com.example.secure.chat.web.model.chat

import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.utils.now
import kotlinx.datetime.LocalDateTime

data class Message(
    val author: Author,
    val text: String,
    var id: Long? = null,
    val timestamp: LocalDateTime = now(),
    val isSecret: Boolean = false,
    private val initialStatus: MessageStatus = MessageStatus.Pending,
) {
    val status: MutableProperty<MessageStatus> = mutableProperty(initialStatus)

    override fun equals(other: Any?): Boolean {
        return other is Message && (id != null && id == other.id)
    }

    override fun hashCode(): Int {
        var result = author.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + isSecret.hashCode()
        result = 31 * result + initialStatus.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}

enum class MessageStatus {
    Pending, Verified, Failed, Unread, Local
}
