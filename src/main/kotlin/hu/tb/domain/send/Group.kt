package hu.tb.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Long,
    val name: String,
    val users: List<User>
)
