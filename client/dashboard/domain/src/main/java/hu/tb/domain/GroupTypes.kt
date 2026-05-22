package hu.tb.domain

sealed interface GroupTypes {
    data class Simple(
        val groupId: Long,
        val otherUsername: String
    ) : GroupTypes

    data class Complex(
        val groupId: Long,
        val participantNames: List<String>
    ): GroupTypes
}