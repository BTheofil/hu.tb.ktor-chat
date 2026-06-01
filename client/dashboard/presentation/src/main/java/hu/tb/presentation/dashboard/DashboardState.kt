package hu.tb.presentation.dashboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import hu.tb.domain.GroupTypes

@Stable
data class DashboardState(
    val username: String = "",
    val groups: List<GroupTypes> = emptyList(),
    val searchText: TextFieldState = TextFieldState()
)
