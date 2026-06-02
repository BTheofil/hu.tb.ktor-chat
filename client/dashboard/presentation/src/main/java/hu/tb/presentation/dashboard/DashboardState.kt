package hu.tb.presentation.dashboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import hu.tb.domain.GroupTypes
import hu.tb.domain.UserMatch

@Stable
data class DashboardState(
    val username: String = "",
    val groups: List<GroupTypes> = emptyList(),
    val searchText: TextFieldState = TextFieldState(),
    val searchResults: List<UserMatch> = emptyList(),
    val isSearching: Boolean = false
)
