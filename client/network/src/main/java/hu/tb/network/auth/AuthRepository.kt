package hu.tb.network.auth

import hu.tb.network.auth.model.response.UserResponse
import hu.tb.domain.LoginInfo
import hu.tb.domain.LoginResult
import hu.tb.domain.ServerStatus
import hu.tb.domain.UserInfo
import hu.tb.network.auth.model.response.UserIdResponse
import hu.tb.network.auth.model.send.LoginSend
import hu.tb.network.auth.model.send.SearchUserSend
import hu.tb.network.dashboard.model.response.UserDetail
import hu.tb.network.dashboard.model.send.UserSearchByNameSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

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
     * the provided login data check if the user profile already exist, if not than create a new account.
     * The server tolerates duplicated usernames, the guard lives here: when the credentials get no
     * token back but the name is already registered, somebody else owns it.
     * @param loginInfo username: String, password: String
     * @return hu.tb.domain.LoginResult - Success with the token and user id, UsernameTaken or Failure
     **/
    suspend fun handleLogin(loginInfo: LoginInfo): LoginResult =
        try {
            val existUserResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
            }

            when (existUserResponse.status) {
                HttpStatusCode.OK -> {
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
                    // The lookup answers 201 on success and plain text on 404, so parsing an
                    // unchecked body would surface a missing user as a serialization failure.
                    if (userIdResponse.status != HttpStatusCode.Created) LoginResult.Failure
                    else LoginResult.Success(
                        UserInfo(
                            userId = userIdResponse.body<UserIdResponse>().id,
                            token = newToken
                        )
                    )
                }

                // no token for these credentials, so either the name is free or it belongs to somebody else
                HttpStatusCode.NotFound -> {
                    val nameResponse = client.post("/searchUserByName") {
                        contentType(ContentType.Application.Json)
                        setBody(UserSearchByNameSend(name = loginInfo.username))
                    }

                    // The lookup always answers OK with a json array, empty when the name is still free.
                    if (nameResponse.status != HttpStatusCode.OK) LoginResult.Failure
                    else if (nameResponse.body<List<UserDetail>>().isNotEmpty()) LoginResult.UsernameTaken
                    else {
                        val newUserResponse = client.post("/createUser") {
                            contentType(ContentType.Application.Json)
                            setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
                        }

                        if (newUserResponse.status != HttpStatusCode.Created) LoginResult.Failure
                        else newUserResponse.body<UserResponse>()
                            .let { LoginResult.Success(UserInfo(userId = it.userId, token = it.token)) }
                    }
                }

                else -> LoginResult.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoginResult.Failure
        }

    suspend fun autoLogin(loginInfo: LoginInfo): UserInfo? {
        try {
            val httpResponse = client.post("/token") {
                contentType(ContentType.Application.Json)
                setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
            }
            return if (httpResponse.status == HttpStatusCode.OK) {
                UserInfo(token = httpResponse.body<String>())
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}