package hu.tb.network.message

import hu.tb.message.domain.Message
import hu.tb.network.message.model.response.MessageResponse
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * One open chat socket. The screen that opened it owns it and has to close it, so a screen can
 * never tear down a socket that belongs to another chat.
 */
class ChatConnection(
    private val session: DefaultClientWebSocketSession,
    private val closeScope: CoroutineScope
) {
    val messages: Flow<Message> = session.incoming
        .consumeAsFlow()
        .filterIsInstance<Frame.Text>()
        .map { frame ->
            val messageDto = Json.decodeFromString<MessageResponse>(frame.readText())
            with(messageDto) {
                Message(
                    id = id,
                    senderId = senderId,
                    content = content,
                    timeStamp = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                )
            }
        }

    suspend fun send(message: String): Boolean =
        try {
            session.send(Frame.Text(message))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    /**
     * Not a suspend function on purpose: this is called from ViewModel.onCleared(), where the
     * viewModelScope is already cancelled and could not run the closing handshake.
     */
    fun close() {
        closeScope.launch {
            try {
                session.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                session.cancel()
            }
        }
    }
}
