package hu.tb.network.dashboard

import hu.tb.domain.GroupTypes
import hu.tb.network.dashboard.model.response.DecodedGroups
import hu.tb.network.dashboard.model.response.UserDetail
import hu.tb.network.dashboard.model.send.DecodeGroupSend
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
    suspend fun getUserGroups(userId: Long): List<GroupTypes>? {
        try {
            val userDetailsResponse = client.post("/searchUserById") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchSend(searchUserId = userId))
            }
            val userDetails = userDetailsResponse.body<UserDetail>()

            val decodeResponse = client.post("/decodeGroup") {
                contentType(ContentType.Application.Json)
                setBody(DecodeGroupSend(userId = userId, groupIds = userDetails.groupIds))
            }
            val allGroups = decodeResponse.body<DecodedGroups>().groups
            return allGroups.map {
                if (it.otherUserName != null) {
                    GroupTypes.Simple(
                        groupId = it.groupId,
                        otherUsername = it.otherUserName
                    )
                } else {
                    GroupTypes.Complex(
                        groupId = it.groupId,
                        participantNames = it.memberNames ?: emptyList()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}