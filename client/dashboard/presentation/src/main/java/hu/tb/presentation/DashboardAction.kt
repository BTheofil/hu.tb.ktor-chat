package hu.tb.presentation

import androidx.compose.runtime.Stable

@Stable
sealed interface DashboardAction {
    data object ProfileClick: DashboardAction, NavigationRequest
    data object FindFriendClick: DashboardAction, NavigationRequest
    data class GroupClick(val groupId: Long): DashboardAction, NavigationRequest
}