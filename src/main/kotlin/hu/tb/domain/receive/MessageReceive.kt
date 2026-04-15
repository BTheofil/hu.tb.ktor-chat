package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class MessageReceive(
    val senderId: Long,
    val content: String
)
