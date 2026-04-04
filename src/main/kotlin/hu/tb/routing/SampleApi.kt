package hu.tb.routing

import hu.tb.repository.SampleRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.sampleApi() {

    val sampleRepository by inject<SampleRepository>()

    get("/case1") {
        val user = sampleRepository.getUserById(1)
        user?.let {
            call.respond(user)
        } ?: call.response.status(HttpStatusCode.NotFound)
    }
}