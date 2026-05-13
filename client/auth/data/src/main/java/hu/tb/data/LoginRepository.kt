package hu.tb.data

import hu.tb.data.model.response.UserResponse
import hu.tb.data.model.send.LoginSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class LoginRepository(
    private val client: HttpClient
) {
    suspend fun handleLogin() {
        try {
            val existUserResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = "", password = ""))
            }
            if (existUserResponse.status == HttpStatusCode.NotFound) {
                val newUserResponse = client.post("/createUser") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginSend(name = "", password = ""))
                }

                val userInfo = newUserResponse.body<UserResponse>()

            } else {
                val newToken = existUserResponse.body<String>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}