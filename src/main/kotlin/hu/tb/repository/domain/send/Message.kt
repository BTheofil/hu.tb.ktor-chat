package hu.tb.repository.domain.send

data class Message(
    val content: String,
    val timestamp: Long,
    val senderId: Long,
    val groupId: Long
)
