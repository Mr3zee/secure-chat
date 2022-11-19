package com.example.auth.server.routing.images

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.auth.server.util.resourceBytes

fun Route.images() {
    route("/images") {
        get("/{image}") {
            val image = call.parameters["image"]!!
            val imageBytes = javaClass.resourceBytes("images/$image") ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val imageType = image.imageType() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            call.respondBytes(imageBytes, imageType, HttpStatusCode.OK)
        }
    }
}

private fun String.imageType(): ContentType? {
    return when (split(".").lastOrNull()) {
        null -> null
        "png" -> ContentType.Image.PNG
        "gif" -> ContentType.Image.GIF
        "jpeg" -> ContentType.Image.JPEG
        "svg" -> ContentType.Image.SVG
        "ico" -> ContentType.Image.XIcon
        else -> ContentType.Image.Any
    }
}
