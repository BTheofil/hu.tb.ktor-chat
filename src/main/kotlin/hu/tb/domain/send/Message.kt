package hu.tb.domain.send

data class Message(
    val content: String,
    val timestamp: Long,
    val senderId: Long,
    val groupId: Long
)
