package hu.tb.network.dashboard.model.send

import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupSend(
    val currentUserId: Long,
    val otherUserId: Long
)