package hu.tb.presentation.dashboard

import androidx.compose.runtime.Stable

@Stable
sealed interface DashboardAction {
    data object Search : DashboardAction
    data object ProfileClick : DashboardAction, NavigationRequest
    data class MakeFriend(val otherUserId: Long) : DashboardAction
    data class GroupClick(
        val groupId: Long,
        val otherUserName: String,
        val hasOtherUserLeft: Boolean
    ) : DashboardAction, NavigationRequest

    data class LongPressGroup(val groupId: Long) : DashboardAction
    data object ConfirmLeaveGroup : DashboardAction
    data object DismissDialog : DashboardAction
}
