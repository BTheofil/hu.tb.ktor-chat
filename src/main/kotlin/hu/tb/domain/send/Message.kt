package hu.tb.domain.send

import hu.tb.datasource.data.repository.MessageId
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: MessageId? = null,
    val content: String,
    val timestamp: Long,
    val senderId: Long,
    val groupId: Long
)
