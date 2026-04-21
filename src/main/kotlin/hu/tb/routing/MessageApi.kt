package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import hu.tb.domain.receive.MessageReceive
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject

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
            println(text)
            /*chatRepository.createMessage(
                message = Message(
                    content = text,
                    timestamp = System.currentTimeMillis(),
                    senderId = 1,
                    groupId = groupId.toLong()
                )
            )*/
        }
    }
}