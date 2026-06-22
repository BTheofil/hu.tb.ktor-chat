package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.MessageDeleteReceive
import hu.tb.domain.receive.MessageHistoryReceive
import hu.tb.domain.send.Message
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject
import java.util.*
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
                            val messageId = chatRepository.createMessage(message = message) //save in db

                            currentRoomSessions.forEach { session ->
                                session.sendSerialized(message.copy(id = messageId))
                            }
                        }

                        is Frame.Close ->
                            close(CloseReason(CloseReason.Codes.NORMAL, "User with $userId id closed"))

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                println("The server get this exception: " + e.localizedMessage)
            } finally {
                currentRoomSessions.remove(this)
                if (currentRoomSessions.isEmpty()) groupConnections.remove(targetGroupId.toLong())
            }
        }
    }

    post("/groupHistory") {
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