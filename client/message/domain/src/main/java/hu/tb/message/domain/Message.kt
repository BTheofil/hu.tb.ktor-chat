package hu.tb.message.domain

import java.time.LocalDateTime

data class Message(
    val senderId: Long,
    val content: String,
    val timeStamp: LocalDateTime,
)