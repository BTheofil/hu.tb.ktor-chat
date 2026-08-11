package hu.tb.network.auth

import hu.tb.network.auth.model.response.UserResponse
import hu.tb.domain.LoginInfo
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
     * tells whether the desired username is already taken by someone with a different password
     * @return true when the name is taken and none of the matches accept this password,
     * false when the name is free or belongs to this very login, null when it could not be decided
     **/
    suspend fun checkDuplicates(loginInfo: LoginInfo): Boolean? =
        try {
            val nameResponse = client.post("/searchUserByName") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchByNameSend(name = loginInfo.username))
            }

            // The lookup always answers OK with a json array, empty when the name is still free.
            if (nameResponse.status != HttpStatusCode.OK) null
            else nameResponse.body<List<UserDetail>>().let { users ->
                users.isNotEmpty() && users.none { it.password == loginInfo.password }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
                    if (userIdResponse.status != HttpStatusCode.Created) null
                    else UserInfo(
                        userId = userIdResponse.body<UserIdResponse>().id,
                        token = newToken
                    )
                }

                in HttpStatusCode.BadRequest..HttpStatusCode.NotFound -> {
                    val newUserResponse = client.post("/createUser") {
                        contentType(ContentType.Application.Json)
                        setBody(LoginSend(name = loginInfo.username, password = loginInfo.password))
                    }

                    if (newUserResponse.status != HttpStatusCode.Created) null
                    else newUserResponse.body<UserResponse>()
                        .let { UserInfo(userId = it.userId, token = it.token) }
                }

                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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