package hu.tb.routing

import io.ktor.http.*
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

fun Route.swaggerApi() {
    swaggerUI("/swagger") {
        info = OpenApiInfo(title = "Endpoints", version = "1.1.2")
        source = OpenApiDocSource.Routing(ContentType.Application.Json) {
            routingRoot.descendants()
        }
    }
}