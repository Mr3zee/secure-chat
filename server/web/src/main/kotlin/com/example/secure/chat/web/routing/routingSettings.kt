package com.example.secure.chat.web.routing

import com.example.secure.chat.web.routing.api.apiRouting
import com.example.secure.chat.web.routing.images.images
import com.example.secure.chat.web.routing.index.indexRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setUpRouting() {
    routing {
        apiRouting()
        indexRouting()
        images()
    }
}
