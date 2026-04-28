package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class MessageDeleteReceive(
    val messageId: Long
)
