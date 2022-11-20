package com.example.auth.server

import com.example.auth.server.db.connection.postgres
import com.example.auth.server.modules.applicationModules
import com.example.auth.server.routing.api.apiRouting
import com.example.auth.server.routing.images.images
import com.example.auth.server.routing.indexRouting
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.environmentProperties
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

val Application.logger by lazy {
    KotlinLogging.logger("Application")
}

@Suppress("unused")
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    configureCORS()

    install(CallLogging) {
        level = this@module.loggerLevel
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Koin) {
        slf4jLogger(this@module.loggerLevel.koinLevel())

        environmentProperties()

        modules(applicationModules)
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

    routing {
        indexRouting()
        apiRouting()
        images()
    }
}
