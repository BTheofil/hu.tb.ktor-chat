package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class MessageHistoryReceive(
    val groupId: Long,
    val offset: Long
)
