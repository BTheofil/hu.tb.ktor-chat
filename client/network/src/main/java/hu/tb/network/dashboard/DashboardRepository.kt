package hu.tb.network.dashboard

import hu.tb.network.dashboard.model.response.UserDetail
import hu.tb.network.dashboard.model.send.UserSearchSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DashboardRepository(
    private val client: HttpClient
) {

    suspend fun getCurrentUser(name: String, password: String)  {
        try {
            val response = client.post("/searchUserByNameAndPw") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchSend(name = name, password = password))
            }
            response.body<UserDetail>()

            client

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}