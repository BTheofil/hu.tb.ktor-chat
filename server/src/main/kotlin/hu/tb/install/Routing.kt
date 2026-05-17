package hu.tb.install

import hu.tb.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.setupRouting() {
    install(RoutingRoot)
    routing {
        serverInfo()
        userApi()
        groupApi()
        messageApi()
        tokenApi()
        swaggerApi()
    }
}
