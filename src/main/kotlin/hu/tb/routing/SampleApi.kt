package hu.tb.routing

import hu.tb.datasource.sample.SampleRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.sampleApi() {

    val sampleRepository by inject<SampleRepository>()

    get("/getSampleUsers") {
        val users = sampleRepository.getUsers()

        call.respond(users)
    }

    post("/insertText") {
        val message = call.receiveText()
        val insertedId = sampleRepository.insertExample(message)
        call.respond(
            status = HttpStatusCode.OK,
            message = insertedId.toString()
        )
    }
}