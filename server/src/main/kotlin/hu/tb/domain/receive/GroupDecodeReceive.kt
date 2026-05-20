package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class GroupDecodeReceive(
    val userId: Long,
    val groupIds: List<Long>
)