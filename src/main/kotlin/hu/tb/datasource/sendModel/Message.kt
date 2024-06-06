package hu.tb.datasource.sendModel

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val sender: String,
    val message: String,
    val timeStamp: Long
)
