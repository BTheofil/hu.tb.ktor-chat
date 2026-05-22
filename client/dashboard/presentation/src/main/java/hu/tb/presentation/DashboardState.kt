package hu.tb.presentation

import androidx.compose.runtime.Stable

@Stable
data class DashboardState(
    val friends: List<String> = emptyList()
)
