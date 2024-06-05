package hu.tb.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val sender: String,
    val message: String,
    val timestamp: Long,
    val sessionId: String
)
