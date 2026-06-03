package hu.tb.network.auth.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userId: Long,
    val token: String
)