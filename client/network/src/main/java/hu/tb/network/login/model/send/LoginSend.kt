package hu.tb.network.login.model.send

import kotlinx.serialization.Serializable

@Serializable
data class LoginSend(
    val name: String,
    val password: String
)