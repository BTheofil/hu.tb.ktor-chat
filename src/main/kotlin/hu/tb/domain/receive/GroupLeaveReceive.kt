package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class GroupLeaveReceive(
    val leaveUserId: Long,
    val targetGroupId: Long
)