package com.example.secure.chat.web.utils

import kotlin.js.Promise

private external val navigator: Navigator

private external interface Navigator {
    val clipboard: Clipboard
}

val clipboard = navigator.clipboard

external interface Clipboard {
    fun writeText(text: String): Promise<Any>
}