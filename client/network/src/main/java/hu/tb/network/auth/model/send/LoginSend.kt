package hu.tb.network.auth.model.send

import kotlinx.serialization.Serializable

@Serializable
data class LoginSend(
    val name: String,
    val password: String
)