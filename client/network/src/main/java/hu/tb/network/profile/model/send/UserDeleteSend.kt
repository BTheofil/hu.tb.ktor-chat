package hu.tb.network.profile.model.send

import kotlinx.serialization.Serializable

@Serializable
data class UserDeleteSend(
    val userId: Long
)
