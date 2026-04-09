package hu.tb.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class UserReceive(
    val name: String,
    val password: String
)
