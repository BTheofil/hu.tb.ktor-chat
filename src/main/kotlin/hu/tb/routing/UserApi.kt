package hu.tb.routing

import hu.tb.repository.ChatRepository
import hu.tb.repository.domain.receive.UserReceive
import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userApi() {

    val chatRepository by inject<ChatRepository>()

    post("/createUser") {
        val newUser = call.receive<UserReceive>()
        val userId = chatRepository.createNewUser(
            username = newUser.name,
            userPassword = newUser.password
        )
        call.respond(message = userId, status = HttpStatusCode.Created)
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
        } ?:
        call.respondText(text = "No user found with $userId", status = HttpStatusCode.NotFound)
    }

    get("/deleteUser") {
        val userId = call.request.queryParameters["userId"]
        if (userId == null) {
            call.respondText(text = "No userId provided", status = HttpStatusCode.NotFound)
            return@get
        }

        chatRepository.deleteUser(userId = userId.toLong())
        call.respondText(text = "User with $userId id deleted", status = HttpStatusCode.OK)
    }
}