package com.example.secure.chat.web.routing.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.apiRouting() {
    route("/api") {
        get("/healthcheck") {
            call.respond("OK")
        }
    }
}
