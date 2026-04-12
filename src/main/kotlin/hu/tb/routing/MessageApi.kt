package hu.tb.routing

import hu.tb.datasource.data.repository.ChatRepository
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import org.koin.ktor.ext.inject

fun Route.messageApi() {

    val chatRepository by inject<ChatRepository>()

    webSocket("/group/{groupId}") {
        val groupId = call.parameters["groupId"]
        if (groupId == null) close(CloseReason(CloseReason.Codes.CLOSED_ABNORMALLY, "No groupId provided"))

        incoming.consumeAsFlow().collect {
            it as Frame.Text
        }

        //val messageId = chatRepository.createMessage()
        // call.respondText(text = "")
    }
}