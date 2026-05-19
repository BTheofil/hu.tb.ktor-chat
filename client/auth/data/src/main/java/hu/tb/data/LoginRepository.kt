package hu.tb.data

import hu.tb.data.model.response.UserResponse
import hu.tb.data.model.send.LoginSend
import hu.tb.domain.LoginInfo
import hu.tb.domain.ServerStatus
import hu.tb.domain.Token
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class LoginRepository(
    private val client: HttpClient
) {
    suspend fun pingServer(): ServerStatus =
        try {
            val response = client.get("/ping")
            return if (response.status == HttpStatusCode.OK) ServerStatus.ALIVE
            else ServerStatus.DEAD
        } catch (e: Exception) {
            e.printStackTrace()
            ServerStatus.DEAD
        }

    /**
     * the provided login data check if the user profile already exist, if not than create a new account
     * @param hu.tb.domain.LoginInfo username: String, password: String
     * @return hu.tb.domain.Token - value:String
     **/
    suspend fun handleLogin(loginInfo: LoginInfo): Token? =
        try {
            val existUserResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
            }
            if (existUserResponse.status in HttpStatusCode.BadRequest..HttpStatusCode.NotFound) {
                val newUserResponse = client.post("/createUser") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
                }

                val userInfo = newUserResponse.body<UserResponse>()
                return Token(userInfo.token)
            } else {
                val newToken = existUserResponse.body<String>()
                return Token(newToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    suspend fun autoLogin(loginInfo: LoginInfo): Token? {
        try {
            val tokenResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
            }
            return Token(value = tokenResponse.body<String>())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}