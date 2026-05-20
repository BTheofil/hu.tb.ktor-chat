package hu.tb.network.dashboard.model.send

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchSend(
    val name: String,
    val password: String
)