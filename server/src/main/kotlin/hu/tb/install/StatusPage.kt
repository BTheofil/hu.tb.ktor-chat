package hu.tb.install

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.installStatusPage() {
    install(StatusPages) {
        exception<Exception> { call, cause ->
            call.respondText(
                text = cause.message ?: "There is been an error :c",
                status = HttpStatusCode.BadRequest
            )
        }
    }
}