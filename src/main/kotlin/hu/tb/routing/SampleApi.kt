package hu.tb.routing

import hu.tb.datasource.sample.SampleRepository
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.sampleApi() {

    val sampleRepository by inject<SampleRepository>()

    get("/getSampleUsers") {
        val users = sampleRepository.getUsers()

        call.respond(users)
    }
}