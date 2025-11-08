package hu.tb.datasource.sample.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSender(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)
