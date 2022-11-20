package com.example.secure.chat.application

import com.example.secure.chat.base.koinLevel
import com.example.secure.chat.base.loggerLevel
import com.example.secure.chat.core.coreModule
import com.example.secure.chat.domain.db.connection.postgres
import com.example.secure.chat.domain.dbModules
import com.example.secure.chat.web.routing.setUpRouting
import com.example.secure.chat.web.webModule
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.koin.environmentProperties
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true

        // webpack-dev-server
        val allowedHosts = listOf("localhost:3000")
        allowedHosts.forEach { host ->
            allowHost(host, listOf("http", "https"))
        }
    }

    install(CallLogging) {
        level = this@module.loggerLevel
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Koin) {
        slf4jLogger(this@module.loggerLevel.koinLevel())

        environmentProperties()

        modules(
            dbModules +
            coreModule +
            webModule
        )
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }

        exception<ContentTransformationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.cause?.message ?: "")
        }
    }

    postgres(isDebug = loggerLevel != Level.INFO)

    setUpRouting()
}
