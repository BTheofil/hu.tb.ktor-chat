package hu.tb.install

import hu.tb.routing.chat
import hu.tb.routing.chatAction
import hu.tb.routing.chatInfo
import hu.tb.routing.sampleApi
import hu.tb.routing.serverInfo
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRouting() {
    install(RoutingRoot)
    routing {
        serverInfo()

        sampleApi()

        chat()
        chatInfo()
        chatAction()
    }
}
