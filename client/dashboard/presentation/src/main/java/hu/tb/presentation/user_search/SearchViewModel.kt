package hu.tb.presentation.user_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.dashboard.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private fun search() {
        viewModelScope.launch {
            val users = dashboardRepository.searchFriend(state.value.searchText.text.toString())
            if (!users.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        matches = users
                    )
                }
            }
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