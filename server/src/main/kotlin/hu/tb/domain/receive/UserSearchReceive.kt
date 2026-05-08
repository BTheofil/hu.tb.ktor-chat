package hu.tb.domain.receive

import kotlinx.serialization.Serializable

sealed interface UserSearchReceive {
    @Serializable
    data class ByTarget(
        val name: String,
        val password: String
    )

    @Serializable
    data class ById(
        val searchUserId: Long
    )

    @Serializable
    data class ByName(
        val name: String
    )
}