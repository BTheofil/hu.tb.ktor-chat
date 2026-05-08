package hu.tb.domain.send

import hu.tb.datasource.data.repository.UserId
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val password: String,
    val groupIds: List<Long> = emptyList()
)

@Serializable
data class UserCreated(
    val userId: UserId,
    val token: String
)
