package hu.tb.domain.send

import kotlinx.serialization.Serializable

@Serializable
sealed interface DecodeGroup {
    @Serializable
    data class Simple(
        val groupId: Long,
        val otherUserName: String
    ) : DecodeGroup

    @Serializable
    data class Complex(
        val groupId: Long,
        val memberNames: List<String>
    ) : DecodeGroup
}