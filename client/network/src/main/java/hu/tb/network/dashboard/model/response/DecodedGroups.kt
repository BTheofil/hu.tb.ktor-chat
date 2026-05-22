package hu.tb.network.dashboard.model.response

import kotlinx.serialization.Serializable

@Serializable
data class DecodedGroups(
    val groups: List<DecodedGroupsItem>
)

@Serializable
data class DecodedGroupsItem(
    val groupId: Long,
    val memberNames: List<String>?,
    val otherUserName: String?
)