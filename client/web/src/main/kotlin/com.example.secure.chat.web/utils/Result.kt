package com.example.secure.chat.web.utils

fun <T> Result<T>.get() = getOrNull() ?: error("Expected success, but was $this")
