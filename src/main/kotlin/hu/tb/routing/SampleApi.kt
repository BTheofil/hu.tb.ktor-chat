package hu.tb.routing

import hu.tb.repository.SampleRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.sampleApi() {

    val sampleRepository by inject<SampleRepository>()

    get("/case1") {
        sampleRepository.insertUser()
    }
}