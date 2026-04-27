package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessageReceive(
    val messageId: Long
)
