package hu.tb.presentation.dashboard

sealed interface DashboardEvent {
    data object AddFriendFailed : DashboardEvent
    data object LeaveGroupFailed : DashboardEvent
    data object LoadGroupsFailed : DashboardEvent
    data object SearchFailed : DashboardEvent
    data object LoggedIn : DashboardEvent
    data object AccountCreated : DashboardEvent
}
