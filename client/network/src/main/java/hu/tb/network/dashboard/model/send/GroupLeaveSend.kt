package hu.tb.network.dashboard.model.send

import kotlinx.serialization.Serializable

@Serializable
data class GroupLeaveSend(
    val leaveUserId: Long,
    val targetGroupId: Long
)
