package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.UserCreateReceive
import hu.tb.service.GenerateInfo
import hu.tb.service.TokenGeneratorService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.tokenApi() {

    val chatRepository by inject<ChatRepository>()
    val generatorService by inject<TokenGeneratorService>()

    post("/token") {
        val userData = call.receive<UserCreateReceive>()

        val registeredUser =
            chatRepository.getUserByNameAndPw(searchedName = userData.name, searchedPw = userData.password)
        if (registeredUser == null) {
            call.respondText(text = "No valid user info provided", status = HttpStatusCode.NotFound)
            return@post
        }

        val generateInfo = GenerateInfo(
            audience = environment.config.property("jwt.audience").getString(),
            issuer = environment.config.property("jwt.issuer").getString(),
            secret = environment.config.propertyOrNull("jwt.secret")?.getString()
        )

        val newToken = generatorService(
            userId = registeredUser.id,
            username = userData.name,
            generateInfo = generateInfo,
        )

        call.respondText(text = newToken, status = HttpStatusCode.OK)
    }
}