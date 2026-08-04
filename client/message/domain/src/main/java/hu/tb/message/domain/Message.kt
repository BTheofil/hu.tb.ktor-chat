package hu.tb.message.domain

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val senderId: Long,
    val content: String,
    val timeStamp: LocalDateTime,
)