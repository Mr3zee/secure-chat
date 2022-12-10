package com.example.secure.chat.web.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


// todo timezones hello?

fun now() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun today() = now().date
