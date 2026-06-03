package hu.tb.network.dashboard.model.response

import kotlinx.serialization.Serializable

@Serializable
data class DecodedGroups(
    val groupId: Long,
    val otherUserName: String
)