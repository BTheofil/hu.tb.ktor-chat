package hu.tb.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*

fun Application.installWebSockets() {
    install(WebSockets)
}
