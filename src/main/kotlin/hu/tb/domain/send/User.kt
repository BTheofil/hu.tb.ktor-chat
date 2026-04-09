package hu.tb.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val password: String,
    val groupIds: List<Long> = emptyList()
)
