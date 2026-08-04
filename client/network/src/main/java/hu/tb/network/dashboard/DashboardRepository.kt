package hu.tb.network.dashboard

import hu.tb.domain.Group
import hu.tb.domain.GroupResult
import hu.tb.domain.UserMatch
import hu.tb.network.dashboard.model.response.DecodedGroups
import hu.tb.network.dashboard.model.response.UserDetail
import hu.tb.network.dashboard.model.send.CreateGroupSend
import hu.tb.network.dashboard.model.send.DecodeGroupSend
import hu.tb.network.dashboard.model.send.GroupLeaveSend
import hu.tb.network.dashboard.model.send.UserSearchByIdSend
import hu.tb.network.dashboard.model.send.UserSearchByNameSend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class DashboardRepository(
    private val client: HttpClient
) {
    suspend fun getUserFriends(userId: Long): List<Group>? {
        try {
            val userDetailsResponse = client.post("/searchUserById") {
                contentType(ContentType.Application.Json)
                setBody(UserSearchByIdSend(searchUserId = userId))
            }
            // A missing user answers NotFound, and any server side exception answers
            // BadRequest, both with a plain text body that body<UserDetail>() can not read.
            if (userDetailsResponse.status != HttpStatusCode.OK) return null
            val userDetails = userDetailsResponse.body<UserDetail>()

            userDetails.groupIds?.let { groupIds ->
                val decodeResponse = client.post("/decodeGroup") {
                    contentType(ContentType.Application.Json)
                    setBody(DecodeGroupSend(userId = userId, groupIds = groupIds))
                }
                // The server answers NotAcceptable with a plain text body when it can
                // decode none of the ids, which happens when every group was left.
                val allGroups = if (decodeResponse.status == HttpStatusCode.OK) {
                    decodeResponse.body<List<DecodedGroups>>()
                } else {
                    emptyList()
                }
                val activeGroups = allGroups.map {
                    Group(
                        groupId = it.groupId,
                        otherUsername = it.otherUserName
                    )
                }

                // A group the user is still a member of but which the server did not
                // decode has no other member left: the other user left the chat.
                val abandonedGroups = (groupIds - activeGroups.map { it.groupId }.toSet())
                    .map { groupId ->
                        Group(
                            groupId = groupId,
                            otherUsername = "",
                            hasOtherUserLeft = true
                        )
                    }

                return activeGroups + abandonedGroups
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
            val currentUserGroupIdSet = currentUserGroupIds.toSet()

            searchedUsers.map { searchUser ->
                UserMatch(
                    id = searchUser.id,
                    name = searchUser.name,
                    isFriend = searchUser.groupIds
                        ?.any { groupId -> groupId in currentUserGroupIdSet } == true
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

    suspend fun leaveGroup(userId: Long, groupId: Long): Boolean =
        try {
            val response = client.delete("/leaveGroup") {
                contentType(ContentType.Application.Json)
                setBody(GroupLeaveSend(leaveUserId = userId, targetGroupId = groupId))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
}