package hu.tb.network.message

import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.message.model.response.Message
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class MessageRepository(
    private val client: HttpClient,
    private val userDatastore: UserDatastoreRepository
) {
    private var session: WebSocketSession? = null

    suspend fun sendMessage(groupId: Long): Flow<Message>? {
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
                ?.map { Json.decodeFromString<Message>(it.readText()) }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

private const val CLIENT_PARAMETER = "targetGroupId"