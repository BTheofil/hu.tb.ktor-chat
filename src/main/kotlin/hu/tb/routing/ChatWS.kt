package hu.tb.routing

import hu.tb.group.GroupController
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject

fun Routing.chat() {
    val groupController by inject<GroupController>()
    webSocket("/chat/{groupId}/{sender}") {
        val roomId = call.parameters["groupId"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "No groupId ID"
            )
        )

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
                        message = frame.readText(),
                        groupId = roomId
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