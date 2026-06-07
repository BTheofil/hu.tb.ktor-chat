package hu.tb.network.message

import hu.tb.datastore.UserDatastoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.first

class MessageRepository(
    private val client: HttpClient,
    private val userDatastore: UserDatastoreRepository
) {
    suspend fun sendMessage(groupId: Long) {
        val token = userDatastore.userdataFlow().first().token

        val response = client.get("/groupConnect") {
            header(HttpHeaders.Authorization, token)
            parameter(CLIENT_PARAMETER, groupId)
        }

        println(response)
    }
}

private const val CLIENT_PARAMETER = "targetGroupId"