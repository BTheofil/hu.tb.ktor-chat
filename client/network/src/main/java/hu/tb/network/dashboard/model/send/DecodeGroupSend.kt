package hu.tb.network.dashboard.model.send

import kotlinx.serialization.Serializable

@Serializable
data class DecodeGroupSend(
    val userId: Long,
    val groupIds: List<Long>
)
