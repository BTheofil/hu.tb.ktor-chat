package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class MessageConnectReceive(
    val targetGroupId: Long
)
