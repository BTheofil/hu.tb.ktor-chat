package hu.tb.install

import hu.tb.routing.sampleApi
import hu.tb.routing.serverInfo
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRouting() {
    install(RoutingRoot)
    routing {
        serverInfo()

        sampleApi()
    }
}
