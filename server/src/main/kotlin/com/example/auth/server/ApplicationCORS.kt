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

        // webpack-dev-server
        val allowedHosts =
            this@configureCORS.propertyOrNull("ktor.deployment.allowedHosts")
                ?.getString()
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()
        allowedHosts.forEach { host ->
            allowHost(host, listOf("http", "https"))
        }
    }
}