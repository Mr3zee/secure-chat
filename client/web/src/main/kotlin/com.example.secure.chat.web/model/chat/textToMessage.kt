package com.example.secure.chat.web.model.chat

val whitespaceRegex = Regex("\\s+")

fun textToMessage(text: String, author: Author): Message {
    return Message(
        author = author,
        text = text.trim(),
    )
}
