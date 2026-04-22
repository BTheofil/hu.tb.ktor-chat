package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.send.Message
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject

fun Route.messageApi() {

    val chatRepository by inject<ChatRepository>()

    authenticate("auth-jwt") {
        webSocket("/group/{groupId}") {
            val groupId = call.parameters["groupId"]
            if (groupId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No groupId provided"))
                return@webSocket
            }

            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()

            incoming.consumeAsFlow().collect { frame ->
                when (frame) {
                    is Frame.Text -> {
                        println(frame.readText())
                        chatRepository.createMessage(
                            message = Message(
                                content = frame.readText(),
                                timestamp = System.currentTimeMillis(),
                                senderId = userId,
                                groupId = groupId.toLong()
                            )
                        )
                    }

                    is Frame.Close -> {
                        close(CloseReason(CloseReason.Codes.NORMAL, "User with $userId id closed"))
                    }

                    else -> {}
                }

            }
        }
    }
}