package com.example.auth.server.routing.api

import io.ktor.server.routing.*
import com.example.auth.server.routing.api.users.userRouting

fun Routing.apiRouting() {
    route("/api") {
        userRouting()
    }
}
