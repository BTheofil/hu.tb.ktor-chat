package hu.tb.install

import hu.tb.routing.groupApi
import hu.tb.routing.userApi
import hu.tb.routing.serverInfo
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRouting() {
    install(RoutingRoot)
    routing {
        serverInfo()
        userApi()
        groupApi()
    }
}
