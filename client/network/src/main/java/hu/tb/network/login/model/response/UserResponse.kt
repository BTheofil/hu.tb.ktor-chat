package hu.tb.network.login.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userId: Long,
    val token: String
)