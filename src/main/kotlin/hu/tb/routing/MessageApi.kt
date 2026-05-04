package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.MessageDeleteReceive
import hu.tb.domain.receive.MessageHistoryReceive
import hu.tb.domain.send.Message
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

fun Route.messageApi() {

    val chatRepository by inject<ChatRepository>()
    val groupConnections = ConcurrentHashMap<Long, MutableSet<DefaultWebSocketServerSession>>()

    authenticate("auth-jwt") {
        webSocket("/groupConnect") {
            val targetGroupId = call.request.queryParameters["targetGroupId"]
            if (targetGroupId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No target group id received :c"))
                return@webSocket
            }

            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()

            val currentRoomSessions = groupConnections.computeIfAbsent(targetGroupId.toLong()) {
                Collections.synchronizedSet(LinkedHashSet())
            }
            currentRoomSessions.add(this)

            try {
                incoming.consumeAsFlow().collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val message = Message(
                                content = frame.readText(),
                                timestamp = System.currentTimeMillis(),
                                senderId = userId,
                                groupId = targetGroupId.toLong()
                            )
                            chatRepository.createMessage(message = message) //save in db

                            currentRoomSessions.forEach { session ->
                                session.sendSerialized(message)
                            }
                        }

                        is Frame.Close ->
                            close(CloseReason(CloseReason.Codes.NORMAL, "User with $userId id closed"))

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                println("The server get this exception: "+ e.localizedMessage)
            } finally {
                currentRoomSessions.remove(this)
                if (currentRoomSessions.isEmpty()) groupConnections.remove(targetGroupId.toLong())
            }
        }
    }

    get("/groupHistory") {
        val messageHistory = call.receive<MessageHistoryReceive>()

        val messagesDomain = chatRepository.getMessageHistory(
            groupId = messageHistory.groupId,
            offset = messageHistory.offset
        )

        call.respond(
            message = messagesDomain,
            status = HttpStatusCode.OK
        )
    }

    delete("/deleteMessage") {
        val messageId = call.receive<MessageDeleteReceive>()
        chatRepository.deleteMessage(messageId = messageId.messageId)
        call.respond(message = "Message deleted with $messageId", status = HttpStatusCode.OK)
    }
}