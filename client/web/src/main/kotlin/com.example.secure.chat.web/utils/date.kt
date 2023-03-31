package com.example.secure.chat.web.utils

import kotlinx.datetime.*


// todo timezones hello?

fun now() = Clock.System.now().zoned()

fun today() = now().date

fun Instant.zoned(): LocalDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
