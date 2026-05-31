package hu.tb.network.dashboard.model.send

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchByNameSend(
    val name: String
)
