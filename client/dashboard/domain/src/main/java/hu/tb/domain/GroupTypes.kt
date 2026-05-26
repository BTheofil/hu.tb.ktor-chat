package hu.tb.domain

sealed interface GroupTypes {
    val groupId: Long
    data class Simple(
        override val groupId: Long,
        val otherUsername: String
    ) : GroupTypes

    data class Complex(
        override val groupId: Long,
        val participantNames: List<String>
    ): GroupTypes
}