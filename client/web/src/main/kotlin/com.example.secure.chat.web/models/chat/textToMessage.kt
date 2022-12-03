package com.example.secure.chat.web.models.chat

import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

val whitespaceRegex = Regex("\\s+")

fun textToMessage(text: String, author: Author, isSecret: Boolean = false): Message {
    val trimmed = text.trim()

    val id = Random(42).nextLong() // todo change
    val now = Clock.System.now()
    val timestamp = now.toLocalDateTime(TimeZone.UTC) // ok?

    return when {
        !isSecret && trimmed.startsWith("/") -> {
            val list = trimmed.split(whitespaceRegex)
            Message.Command(
                author = author,
                id = id,
                timestamp = timestamp,
                text = trimmed,
                status = mutableStateOf(MessageStatus.Local),
                command = list.first(),
                arguments = list.drop(1)
            )
        }

        else -> {
            Message.Text(
                author = author,
                id = id,
                timestamp = timestamp,
                text = trimmed,
                status = mutableStateOf(MessageStatus.Local)
            )
        }
    }
}
