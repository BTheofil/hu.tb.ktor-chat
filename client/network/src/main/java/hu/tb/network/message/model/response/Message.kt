package hu.tb.network.message.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val timestamp: Long,
    val senderId: Long,
    val groupId: Long
)
