package hu.tb.network.message

import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.message.model.response.Message
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.first

class MessageRepository(
    private val client: HttpClient,
    private val userDatastore: UserDatastoreRepository
) {
    suspend fun sendMessage(groupId: Long) {
        try {
            val token = userDatastore.userdataFlow().first().token
            val session = client.webSocket(
                path = "/groupConnect",
                request = {
                    parameter(CLIENT_PARAMETER, groupId)
                    header(HttpHeaders.Authorization, "Bearer $token")
                },
                block = {
                    println("hello")
                    for (frame in incoming) {
                        val message = receiveDeserialized<Message>()
                        println(message)
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private const val CLIENT_PARAMETER = "targetGroupId"