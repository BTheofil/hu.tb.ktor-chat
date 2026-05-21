package hu.tb.routing

import io.ktor.http.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

fun Route.swaggerApi() {
    swaggerUI("/swagger") {
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }
}