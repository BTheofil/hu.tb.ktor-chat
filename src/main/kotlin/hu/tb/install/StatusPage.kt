package hu.tb.install

import com.mongodb.MongoCommandException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.installStatusPage() {
    install(StatusPages) {
        exception<MongoCommandException> { call, cause ->
            call.respondText(
                text = "No permission $cause",
                status = HttpStatusCode.Locked
            )
        }
    }
}