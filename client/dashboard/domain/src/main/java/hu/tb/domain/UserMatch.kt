package hu.tb.domain

data class UserMatch(
    val id: Long,
    val name: String,
    val isFriend: Boolean = false
)
