package hu.tb.network.message.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    val content: String,
    val timestamp: Long,
    val senderId: Long,
    val groupId: Long
)
