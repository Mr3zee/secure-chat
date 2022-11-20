@file:Suppress("unused")

package com.example.secure.chat.web.routing.utils

import io.ktor.http.*

fun <T> Parameters.saveReceive(name: String, map: (String) -> T?): T {
    return get(name)?.let(map) ?: throw IllegalArgumentException("Invalid $name parameter")
}

fun Parameters.saveReceive(name: String): String {
    return saveReceive(name) { it }
}
