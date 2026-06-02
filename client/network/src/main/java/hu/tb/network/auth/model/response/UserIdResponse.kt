package hu.tb.network.auth.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserIdResponse(
    val id: Long
)
