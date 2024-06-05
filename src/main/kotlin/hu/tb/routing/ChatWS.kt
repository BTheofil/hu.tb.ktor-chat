package hu.tb.routing

import hu.tb.model.Member
import hu.tb.model.Message
import hu.tb.plugins.ChatSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val memoryDatabase = mutableListOf<Message>()

private val members = ConcurrentHashMap<String, Member>()

fun Routing.chat() {
    webSocket("/chat") {

        val currentSession = this.call.sessions.get<ChatSession>()

        members[currentSession!!.name] = Member(
            name = currentSession.name,
            socket = this
        )

        this.incoming.consumeEach { frame ->
            if (frame is Frame.Text){

                val message = Message(
                    sender = currentSession.name,
                    message = frame.readText(),
                    timestamp = System.currentTimeMillis(),
                    sessionId = currentSession.sessionId
                )

                memoryDatabase.add(message)
                println(memoryDatabase.size)

                members.values.forEach { member ->
                    val parsedMessage = Json.encodeToString(message)
                    member.socket.send(Frame.Text(parsedMessage))
                }
            }
        }

    }
}


fun Route.getMessage(){
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            memoryDatabase
        )
    }
}