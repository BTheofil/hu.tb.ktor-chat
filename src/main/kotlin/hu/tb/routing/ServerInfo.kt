package hu.tb.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Routing.serverInfo() {
    get("/ping") {
        call.respondText(
            text = "pong",
            status = HttpStatusCode.OK
        )
    }
}