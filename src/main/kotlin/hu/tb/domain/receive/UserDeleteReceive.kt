package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class UserDeleteReceive(
    val userId: Long
)
