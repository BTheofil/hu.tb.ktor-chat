package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
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

        val senderId = call.receive<Long>()

        incoming.consumeAsFlow().collect { frame ->
            val text = (frame as Frame.Text).readText()
            chatRepository.createMessage(
                message = Message(
                    content = text,
                    timestamp = System.currentTimeMillis(),
                    senderId = senderId,
                    groupId = groupId.toLong()
                )
            )
        }

        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))

        /*send("Please enter your name")
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            if (receivedText.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            } else {
                send(Frame.Text("Hi, $receivedText!"))
            }
        }*/

        //val messageId = chatRepository.createMessage()
        // call.respondText(text = "")
    }
}