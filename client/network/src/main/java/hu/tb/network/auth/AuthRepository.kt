package hu.tb.network.auth

import hu.tb.network.auth.model.response.UserResponse
import hu.tb.domain.LoginInfo
import hu.tb.domain.ServerStatus
import hu.tb.domain.UserInfo
import hu.tb.network.auth.model.response.UserIdResponse
import hu.tb.network.auth.model.send.LoginSend
import hu.tb.network.auth.model.send.SearchUserSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.ranges.rangeTo

class AuthRepository(
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
    suspend fun handleLogin(loginInfo: LoginInfo): UserInfo? =
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
                return UserInfo(userId = userInfo.userId, userInfo.token)
            } else {
                // user already in db just get a new token
                val newToken = existUserResponse.body<String>()

                // get user id also for dataStore
                val userIdResponse = client.post("/searchUserByNameAndPw") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        SearchUserSend(
                            name = loginInfo.username,
                            password = loginInfo.password
                        )
                    )
                }
                val userId = userIdResponse.body<UserIdResponse>().id

                return UserInfo(userId = userId, token = newToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    suspend fun autoLogin(loginInfo: LoginInfo): UserInfo? {
        try {
            val tokenResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
            }
            return UserInfo(token = tokenResponse.body<String>())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}