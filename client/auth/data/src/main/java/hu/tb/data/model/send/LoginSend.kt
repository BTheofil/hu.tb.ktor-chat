package hu.tb.data.model.send

import kotlinx.serialization.Serializable

@Serializable
data class LoginSend(
    val name: String,
    val password: String
)