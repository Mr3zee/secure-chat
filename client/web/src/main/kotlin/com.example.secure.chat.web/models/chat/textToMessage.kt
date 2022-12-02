package com.example.secure.chat.web.models.chat

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

private val whitespaceRegex = Regex("\\s+")

fun textToMessage(text: String, author: Author): Message {
    val trimmed = text.trim()

    val id = Random(42).nextLong() // todo change
    val now = Clock.System.now()
    val timestamp = now.toLocalDateTime(TimeZone.UTC) // ok?

    return when {
        trimmed.startsWith("/") -> {
            val list = trimmed.split(whitespaceRegex)
            Message.Command(author, id, timestamp, trimmed, list.first(), list.drop(1))
        }

        else -> {
            Message.Text(author, id, timestamp, trimmed)
        }
    }
}
