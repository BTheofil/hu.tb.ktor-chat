package hu.tb.network.dashboard.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserDetail(
    val id: Long,
    val name: String,
    val password: String,
    val groupIds: List<Long>
)
