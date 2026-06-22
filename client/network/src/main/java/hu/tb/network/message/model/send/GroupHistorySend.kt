package hu.tb.network.message.model.send

import kotlinx.serialization.Serializable

@Serializable
data class GroupHistorySend(
    val groupId: Int,
    val offset: Int
)
