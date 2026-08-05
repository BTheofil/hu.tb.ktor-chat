package hu.tb.domain

data class Group(
    val groupId: Long,
    val otherUsername: String,
    val hasOtherUserLeft: Boolean = false
)