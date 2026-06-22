package hu.tb.network.message

import hu.tb.datastore.UserDatastoreRepository
import hu.tb.message.domain.Message
import hu.tb.network.message.model.response.MessageResponse
import hu.tb.network.message.model.send.GroupHistorySend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MessageRepository(
    private val client: HttpClient,
    private val userDatastore: UserDatastoreRepository
) {
    private var session: WebSocketSession? = null

    suspend fun getMessageHistory(groupId: Int, page: Int = 0): List<Message>? =
        try {
            val response = client.post("/groupHistory") {
                contentType(ContentType.Application.Json)
                setBody(GroupHistorySend(groupId = groupId, offset = page))
            }
            val messageDtoList = response.body<List<MessageResponse>>()

            messageDtoList.map {
                Message(
                    senderId = it.senderId,
                    content = it.content,
                    timeStamp = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.timestamp),
                        ZoneId.systemDefault()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    suspend fun connectGroupObserver(groupId: Long): Flow<Message>? =
        try {
            val token = userDatastore.userdataFlow().first().token
            session = client.webSocketSession(
                port = 8080,
                path = "/groupConnect",
                block = {
                    parameter(CLIENT_PARAMETER, groupId)
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            )

            return session?.incoming
                ?.consumeAsFlow()
                ?.filterIsInstance<Frame.Text>()
                ?.map {
                    val messageDto = Json.decodeFromString<MessageResponse>(it.readText())
                    with(messageDto) {
                        Message(
                            senderId = this.senderId,
                            content = this.content,
                            timeStamp = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(this.timestamp),
                                ZoneId.systemDefault()
                            )
                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    suspend fun sendMessage(message: String) =
        try {
            session?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
}

private const val CLIENT_PARAMETER = "targetGroupId"