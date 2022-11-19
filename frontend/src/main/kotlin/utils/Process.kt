@file:Suppress("PropertyName")

package utils

external val process: Process

external interface Process {
    val env: ProcessEnvironment
}

external interface ProcessEnvironment {
    val NODE_ENV: String?
}

fun isDevEnv(): Boolean {
    return process.env.NODE_ENV == "development"
}
