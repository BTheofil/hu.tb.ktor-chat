package hu.tb.domain

sealed interface GroupsResult {
    data class Success(val groups: List<Group>) : GroupsResult
    data object Failure : GroupsResult
}
