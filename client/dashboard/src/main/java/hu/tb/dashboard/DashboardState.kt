package hu.tb.dashboard

import androidx.compose.runtime.Stable

@Stable
data class DashboardState(
    val friends: List<String> = emptyList()
)
