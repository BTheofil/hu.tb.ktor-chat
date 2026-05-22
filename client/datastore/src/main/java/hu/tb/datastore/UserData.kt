package hu.tb.datastore

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: Long = -1L,
    val name: String = "",
    val password: String = "",
    val token: String = "",
    val tokenRefreshDate: String = ""
)