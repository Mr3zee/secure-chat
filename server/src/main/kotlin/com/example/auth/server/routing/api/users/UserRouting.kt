package com.example.auth.server.routing.api.users

import ServerGreeting
import User
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.example.auth.server.services.UserService

fun Route.userRouting() {
    val userService by inject<UserService>()

    route("/users") {
        post {
            val user = call.receive<User>()
            val allUsers = userService.addUser(user)
            call.respond(allUsers to ServerGreeting("Hi from server, ${user.name}"))
        }

        get {
            val allUsers = userService.getAllUsers()
            call.respond(allUsers)
        }
    }
}
