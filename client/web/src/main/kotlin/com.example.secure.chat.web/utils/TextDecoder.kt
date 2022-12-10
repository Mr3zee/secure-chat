package com.example.secure.chat.web.utils

import org.khronos.webgl.Uint8Array

val decoder = TextDecoder()

external class TextDecoder {
    fun decode(bytes: Uint8Array): String
}
