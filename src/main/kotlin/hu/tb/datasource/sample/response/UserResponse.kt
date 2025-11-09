package hu.tb.datasource.sample.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)
