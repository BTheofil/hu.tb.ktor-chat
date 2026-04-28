package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.UserCreateReceive
import hu.tb.domain.receive.UserDeleteReceive
import hu.tb.domain.receive.UserSearchReceive
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
        val newUser = call.receive<UserCreateReceive>()
        val userId = chatRepository.createNewUser(
            username = newUser.name,
            userPassword = newUser.password
        )

        val generateInfo = GenerateInfo(
            audience = environment.config.property("jwt.audience").getString(),
            issuer = environment.config.property("jwt.issuer").getString(),
            secret = environment.config.propertyOrNull("jwt.secret")?.getString()
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
        val searchUser = call.receive<UserSearchReceive.ById>()

        val user = chatRepository.getUserById(userId = searchUser.searchUserId)
        user?.let {
            call.respond(message = user, status = HttpStatusCode.Found)
        } ?: call.respondText(text = "No user found with ${searchUser.searchUserId}", status = HttpStatusCode.NotFound)
    }

    get("/searchUserByNameAndPw") {
        val searchUser = call.receive<UserSearchReceive.ByTarget>()

        val user = chatRepository.getUserByNameAndPw(searchUser.name, searchUser.password)
        if (user != null) {
            call.respond(message = user, status = HttpStatusCode.Created)
        } else {
            call.respondText(text = "No user found", status = HttpStatusCode.NotFound)
        }
    }

    get("/searchUserByName") {
        val searchUser = call.receive<UserSearchReceive.ByName>()

        val names = chatRepository.getUserByName(searchUser.name)
        call.respond(message = names, status = HttpStatusCode.OK)
    }

    delete("/deleteUser") {
        val userData = call.receive<UserDeleteReceive>()

        chatRepository.deleteUser(userId = userData.userId)
        call.respondText(text = "User with ${userData.userId} id deleted", status = HttpStatusCode.OK)
    }
}