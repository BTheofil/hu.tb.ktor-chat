package hu.tb.domain.send

sealed interface DecodeGroup {
    data class Simple(
        val groupId: Long,
        val otherUserName: String
    ): DecodeGroup

    data class Complex(
        val groupId: Long,
        val memberNames: List<String>
    ): DecodeGroup
}