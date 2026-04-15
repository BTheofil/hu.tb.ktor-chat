package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.MessageReceive
import hu.tb.domain.receive.UserReceive
import hu.tb.domain.send.Message
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject
import kotlin.time.Clock

fun Route.messageApi() {

    val chatRepository by inject<ChatRepository>()

    webSocket("/group/{groupId}") {
        val groupId = call.parameters["groupId"]
        if (groupId == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No groupId provided"))
            return@webSocket
        }

        //todo implement auth for get user id
        val user = receiveDeserialized<MessageReceive>()

        incoming.consumeAsFlow().collect { frame ->
            val text = (frame as Frame.Text).readText()
            chatRepository.createMessage(
                message = Message(
                    content = text,
                    timestamp = System.currentTimeMillis(),
                    senderId = 1,
                    groupId = groupId.toLong()
                )
            )
        }
    }
}