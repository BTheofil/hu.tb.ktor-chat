package hu.tb.network.message.model.send

import kotlinx.serialization.Serializable

@Serializable
data class MessageDeleteSend(
    val messageId: Long
)
