package hu.tb.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.dashboard.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            val groups = dashboardRepository.getUserFriends(userId = userData.id)
            _state.update {
                it.copy(
                    username = userData.name,
                    groups = groups ?: emptyList()
                )
            }
        }
    }

    fun action(action: DashboardAction) {
        when (action) {
            DashboardAction.Search -> searchForFriend()
            else -> return
        }
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
            if (!users.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        searchResults = users
                    )
                }
            }
            _state.update { it.copy(isSearching = false) }
        }
    }

    private fun connect(otherUserId: Long) {
        viewModelScope.launch {
            val userId = userDatastoreRepository.userdataFlow().first().id
            val result = dashboardRepository.makeGroup(
                userId = userId,
                otherUserId = otherUserId
            )
        }
    }
}