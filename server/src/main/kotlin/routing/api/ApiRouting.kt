package routing.api

import io.ktor.server.routing.*
import routing.api.users.userRouting

fun Routing.apiRouting() {
    route("/api") {
        userRouting()
    }
}
