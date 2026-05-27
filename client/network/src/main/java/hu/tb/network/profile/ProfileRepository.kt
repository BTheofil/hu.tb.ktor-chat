package hu.tb.network.profile

import hu.tb.network.profile.model.send.UserDeleteSend
import hu.tb.profile.domain.ProfileDeleteStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class ProfileRepository(
    private val client: HttpClient
) {
    suspend fun deleteProfile(userId: Long): ProfileDeleteStatus =
        try {
            val response = client.delete("/deleteUser") {
                contentType(ContentType.Application.Json)
                setBody(UserDeleteSend(userId = userId))
            }
            if (response.status == HttpStatusCode.OK) ProfileDeleteStatus.SUCCESS
            else ProfileDeleteStatus.FAILED
        } catch (e: Exception) {
            e.printStackTrace()
            ProfileDeleteStatus.FAILED
        }
}