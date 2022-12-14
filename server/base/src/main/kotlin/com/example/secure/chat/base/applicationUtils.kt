package com.example.secure.chat.base

import io.ktor.server.application.*
import org.slf4j.event.Level

@Suppress("unused")
fun Application.isDevEnv(): Boolean {
    val env = propertyOrNull("ktor.environment")?.getString()
    return env == "development"
}

fun Application.propertyOrNull(path: String) = environment.config.propertyOrNull(path)

fun Application.property(path: String) = propertyOrNull(path) ?: error("Expected Application property $path")

val Application.callLoggerLevel: Level
    get() = propertyOrNull("ktor.logger.call.level")?.let {
        Level.valueOf(it.getString())
    } ?: Level.INFO

val Application.koinLoggerLevel: org.koin.core.logger.Level
    get() = propertyOrNull("ktor.logger.koin.level")?.let {
        org.koin.core.logger.Level.valueOf(it.getString())
    } ?: org.koin.core.logger.Level.INFO

