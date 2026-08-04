package hu.tb.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.GroupResult
import hu.tb.network.dashboard.DashboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _event = Channel<DashboardEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            _state.update { it.copy(username = userData.name) }
            loadGroups(userId = userData.id)
        }
    }

    fun action(action: DashboardAction) {
        when (action) {
            DashboardAction.Search -> searchForFriend()
            is DashboardAction.MakeFriend -> makeFriend(action.otherUserId)
            is DashboardAction.LongPressGroup -> showLeaveDialog(action.groupId)
            DashboardAction.ConfirmLeaveGroup -> leaveGroup()
            DashboardAction.DismissDialog -> dismissDialog()
            else -> return
        }
    }

    private suspend fun loadGroups(userId: Long) {
        val groups = dashboardRepository.getUserFriends(userId = userId)
        _state.update { it.copy(groups = groups ?: emptyList()) }
    }

    private fun searchForFriend() {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            val userData = userDatastoreRepository.userdataFlow().first()
            val users = dashboardRepository.searchFriend(
                currentUserId = userData.id,
                currentUserGroupIds = state.value.groups.map { it.groupId },
                searchName = state.value.searchText.text.toString()
            )
            // Always replace the results, otherwise a failed or empty search leaves
            // the previous list on screen and stale rows stay tappable.
            _state.update {
                it.copy(
                    searchResults = users ?: emptyList(),
                    isSearching = false
                )
            }
            if (users == null) {
                _event.send(DashboardEvent.SearchFailed)
            }
        }
    }

    private fun makeFriend(otherUserId: Long) {
        viewModelScope.launch {
            val userId = userDatastoreRepository.userdataFlow().first().id
            val result = dashboardRepository.makeGroup(
                userId = userId,
                otherUserId = otherUserId
            )
            if (result == GroupResult.CREATED) {
                loadGroups(userId = userId)
            } else {
                _event.send(DashboardEvent.AddFriendFailed)
            }
        }
    }

    private fun showLeaveDialog(groupId: Long) {
        _state.update { it.copy(groupIdPendingLeave = groupId) }
    }

    private fun dismissDialog() {
        _state.update { it.copy(groupIdPendingLeave = null) }
    }

    private fun leaveGroup() {
        val groupId = state.value.groupIdPendingLeave ?: return
        viewModelScope.launch {
            val userId = userDatastoreRepository.userdataFlow().first().id
            val isLeft = dashboardRepository.leaveGroup(userId = userId, groupId = groupId)
            _state.update { it.copy(groupIdPendingLeave = null) }
            if (isLeft) {
                loadGroups(userId = userId)
            } else {
                _event.send(DashboardEvent.LeaveGroupFailed)
            }
        }
    }
}
