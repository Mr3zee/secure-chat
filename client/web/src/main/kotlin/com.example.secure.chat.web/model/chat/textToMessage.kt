package com.example.secure.chat.web.model.chat

val whitespaceRegex = Regex("\\s+")

fun textToMessage(text: String, author: Author, isSecret: Boolean): Message {
    return Message(
        author = author,
        text = text.trim(),
        isSecret = isSecret
    )
}
