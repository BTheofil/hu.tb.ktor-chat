package hu.tb.plugins

import hu.tb.routing.chat
import hu.tb.routing.chatInfo
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Routing) {
        chat()
        chatInfo()
    }
}
