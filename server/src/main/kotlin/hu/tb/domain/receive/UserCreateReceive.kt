package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateReceive(
    val name: String,
    val password: String
)
