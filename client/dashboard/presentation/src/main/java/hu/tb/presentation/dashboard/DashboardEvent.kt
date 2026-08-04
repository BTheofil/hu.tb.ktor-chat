package hu.tb.presentation.dashboard

sealed interface DashboardEvent {
    data object AddFriendFailed : DashboardEvent
    data object LeaveGroupFailed : DashboardEvent
    data object SearchFailed : DashboardEvent
}
