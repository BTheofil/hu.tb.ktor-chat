package hu.tb.routing

import hu.tb.group.GroupController
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject

fun Routing.chat() {
    val groupController by inject<GroupController>()
    webSocket("/chat/{roomId}/{sender}") {
        /*val roomId = call.parameters["roomId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "No room ID"
            )
        )*/

        val sender = call.parameters["sender"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "No sender"
            )
        )

        try {
            //1. join to room
            groupController.join(
                name = sender,
                socketSession = this
            )

            //2. consume the messages
            incoming.consumeEach { frame: Frame ->
                if (frame is Frame.Text) {
                    groupController.sendMessage(
                        sender = sender,
                        message = frame.readText()
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            groupController.leave(
                sender
            )
        }
    }
}