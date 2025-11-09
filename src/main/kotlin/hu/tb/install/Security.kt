package hu.tb.install

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*

data class ChatSession(
    val name: String,
    val sessionId: String
)

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("CHAT_SESSION")
    }

    intercept(Plugins) {
        if(call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(username, generateSessionId()))
        }
    }
}
