package com.example.auth.server.util

fun Class<*>.resourceBytes(resourcePath: String): ByteArray? {
    return classLoader.getResourceAsStream(resourcePath).use { it?.readBytes() }
}
