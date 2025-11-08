package hu.tb.plugins

import hu.tb.routing.chat
import hu.tb.routing.chatAction
import hu.tb.routing.chatInfo
import hu.tb.routing.serverInfo
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRouting() {
    routing {
        serverInfo()

        chat()
        chatInfo()
        chatAction()
    }
}
