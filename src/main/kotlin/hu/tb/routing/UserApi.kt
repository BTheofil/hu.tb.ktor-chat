package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.UserReceive
import hu.tb.domain.send.UserCreated
import hu.tb.service.GenerateInfo
import hu.tb.service.TokenGeneratorService
import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userApi() {

    val chatRepository by inject<ChatRepository>()
    val generatorService by inject<TokenGeneratorService>()

    post("/createUser") {
        val newUser = call.receive<UserReceive>()
        val userId = chatRepository.createNewUser(
            username = newUser.name,
            userPassword = newUser.password
        )

        val generateInfo = GenerateInfo(
            audience = environment.config.property("jwt.audience").getString(),
            issuer = environment.config.property("jwt.issuer").getString()
        )

        val token = generatorService(
            userId = userId,
            username = newUser.name,
            generateInfo = generateInfo
        )

        call.respond(
            message = UserCreated(
                userId = userId,
                token = token
            ),
            status = HttpStatusCode.Created
        )
    }

    get("/searchUserById") {
        val userId = call.request.queryParameters["userId"]?.toLong()
        if (userId == null) {
            call.respondText(text = "No userId provided", status = HttpStatusCode.NotFound)
            return@get
        }

        val user = chatRepository.getUserById(userId = userId)
        user?.let {
            call.respond(message = user, status = HttpStatusCode.Found)
        } ?: call.respondText(text = "No user found with $userId", status = HttpStatusCode.NotFound)
    }

    post("/searchUserByNameAndPw") {
        val searchedUser = call.receive<UserReceive>()
        val user = chatRepository.getUserByNameAndPw(searchedUser.name, searchedUser.password)
        if (user != null) {
            call.respond(message = user, status = HttpStatusCode.Created)
        } else {
            call.respondText(text = "No user found", status = HttpStatusCode.NotFound)
        }
    }

    get("/searchUsersName") {
        val requestedNameSearch = call.request.queryParameters["searchName"]
        if (requestedNameSearch != null) {
            val names = chatRepository.getUserByName(requestedNameSearch)
            call.respond(message = names, status = HttpStatusCode.OK)
        } else {
            call.respondText(text = "No name in query parameter", status = HttpStatusCode.NotFound)
        }
    }

    delete("/deleteUser") {
        val userId = call.request.queryParameters["userId"]
        if (userId == null) {
            call.respondText(text = "No userId provided", status = HttpStatusCode.NotFound)
            return@delete
        }

        chatRepository.deleteUser(userId = userId.toLong())
        call.respondText(text = "User with $userId id deleted", status = HttpStatusCode.OK)
    }
}