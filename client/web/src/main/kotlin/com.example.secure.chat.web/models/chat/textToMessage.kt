package com.example.secure.chat.web.models.chat

import androidx.compose.runtime.mutableStateOf
import com.example.secure.chat.web.utils.now
import kotlin.random.Random

val whitespaceRegex = Regex("\\s+")

fun textToMessage(text: String, author: Author): Message {
    val id = Random(42).nextLong() // todo change

    return Message(
        author = author,
        id = id,
        timestamp = now(),
        text = text.trim(),
        status = mutableStateOf(MessageStatus.Local)
    )
}
