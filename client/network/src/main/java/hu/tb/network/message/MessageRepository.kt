package hu.tb.network.message

import hu.tb.datastore.UserDatastoreRepository
import hu.tb.message.domain.Message
import hu.tb.network.message.model.response.MessageResponse
import hu.tb.network.message.model.send.GroupHistorySend
import hu.tb.network.message.model.send.MessageDeleteSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MessageRepository(
    private val client: HttpClient,
    private val userDatastore: UserDatastoreRepository
) {
    // Outlives any single screen so a socket can still be closed gracefully while the owning
    // ViewModel is being destroyed.
    private val closeScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getMessageHistory(groupId: Int, page: Int = 0): List<Message>? =
        try {
            val response = client.post("/groupHistory") {
                contentType(ContentType.Application.Json)
                setBody(GroupHistorySend(groupId = groupId, offset = page))
            }
            val messageDtoList = response.body<List<MessageResponse>>()

            messageDtoList.map {
                Message(
                    id = it.id,
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

    /**
     * Opens a socket for one chat. Returns null when the connection can not be established, for
     * example a refused handshake, an expired token or an unreachable server.
     */
    suspend fun openChatConnection(groupId: Long): ChatConnection? =
        try {
            val token = userDatastore.userdataFlow().first().token
            val session = client.webSocketSession(
                port = 8080,
                path = "/groupConnect",
                block = {
                    parameter(CLIENT_PARAMETER, groupId)
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            )

            ChatConnection(session = session, closeScope = closeScope)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    suspend fun deleteMessage(messageId: Long): Boolean =
        try {
            val response = client.delete("/deleteMessage") {
                contentType(ContentType.Application.Json)
                setBody(MessageDeleteSend(messageId = messageId))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
}

private const val CLIENT_PARAMETER = "targetGroupId"