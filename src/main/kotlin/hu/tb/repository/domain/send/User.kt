package hu.tb.repository.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val password: String
)
