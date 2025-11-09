package hu.tb.install

import io.ktor.server.application.*
import io.ktor.server.websocket.*

fun Application.installWebSockets() {
    install(WebSockets)
}
