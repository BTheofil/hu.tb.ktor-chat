package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateReceive(
    val currentUserId: Long,
    val otherUserId: Long
)
