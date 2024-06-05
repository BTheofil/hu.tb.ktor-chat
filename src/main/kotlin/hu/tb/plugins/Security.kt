package hu.tb.plugins

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.websocket.*

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
