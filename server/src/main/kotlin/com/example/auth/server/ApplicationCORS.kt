package com.example.auth.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        val allowedHosts =
            this@configureCORS.propertyOrNull("ktor.deployment.allowedHosts")
                ?.getString()
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()

        allowedHosts.forEach { host ->
            this@configureCORS.logger.info { "Register host as allowed in CORS: http://$host, https://$host" }
            allowHost(host, listOf("http", "https"))
        }

    }
}