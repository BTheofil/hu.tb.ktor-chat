package hu.tb.network.dashboard

import hu.tb.domain.GroupResult
import hu.tb.domain.GroupTypes
import hu.tb.domain.UserMatch
import hu.tb.network.dashboard.model.response.DecodedGroups
import hu.tb.network.dashboard.model.response.UserDetail
import hu.tb.network.dashboard.model.send.CreateGroupSend
import hu.tb.network.dashboard.model.send.DecodeGroupSend
import hu.tb.network.dashboard.model.send.UserSearchByIdSend
import hu.tb.network.dashboard.model.send.UserSearchByNameSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class DashboardRepository(
    private val client: HttpClient
) {
    suspend fun getUserFriends(userId: Long): List<GroupTypes>? {
        try {
            val userDetailsResponse = client.post("/searchUserById") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchByIdSend(searchUserId = userId))
            }
            val userDetails = userDetailsResponse.body<UserDetail>()

            userDetails.groupIds?.let { groupIds ->
                val decodeResponse = client.post("/decodeGroup") {
                    contentType(ContentType.Application.Json)
                    setBody(DecodeGroupSend(userId = userId, groupIds = groupIds))
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
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun searchFriend(
        currentUserId: Long,
        currentUserGroupIds: List<Long>,
        searchName: String
    ): List<UserMatch>? =
        try {
            val searchUsersResponse = client.post("/searchUserByName") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchByNameSend(name = searchName))
            }
            val searchedUsers =
                searchUsersResponse.body<List<UserDetail>>().filter { it.id != currentUserId }

            val isCurrentUserHasGroupWithSearched =
                searchedUsers.mapNotNull { searchUser -> searchUser.groupIds?.any { groupId -> groupId in currentUserGroupIds } }

            searchedUsers.mapIndexed { index, searchUser ->
                UserMatch(
                    id = searchUser.id,
                    name = searchUser.name,
                    isFriend = isCurrentUserHasGroupWithSearched.getOrNull(index) ?: false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    suspend fun makeGroup(userId: Long, otherUserId: Long): GroupResult =
        try {
            val groupResponse = client.post("/createGroup") {
                contentType(ContentType.Application.Json)
                setBody(CreateGroupSend(currentUserId = userId, otherUserId = otherUserId))
            }

            return if (groupResponse.status == HttpStatusCode.Created) GroupResult.CREATED
            else GroupResult.FAILED_TO_CREATE
        } catch (e: Exception) {
            e.printStackTrace()
            return GroupResult.FAILED_TO_CREATE
        }
}