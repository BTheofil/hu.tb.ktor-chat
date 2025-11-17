package hu.tb.routing

import hu.tb.datasource.sample.SampleRepository
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.sampleApi() {

    val sampleRepository by inject<SampleRepository>()

    get("/countUsers") {
        val counted = sampleRepository.countUsers()

        call.respond(counted)
    }

    get("/testAddUser") {
        val id = sampleRepository.addUser()

        call.respond(id)
    }
}