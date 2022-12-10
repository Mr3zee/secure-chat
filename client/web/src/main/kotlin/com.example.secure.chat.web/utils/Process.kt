@file:Suppress("PropertyName")

package com.example.secure.chat.web.utils

external val process: Process

external interface Process {
    val env: ProcessEnvironment
}

external interface ProcessEnvironment {
    val NODE_ENV: String?
}

@Suppress("unused")
fun isDevEnv(): Boolean {
    return process.env.NODE_ENV == "development"
}
