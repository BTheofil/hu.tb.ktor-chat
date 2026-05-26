package hu.tb.presentation

import androidx.compose.runtime.Stable
import hu.tb.domain.GroupTypes

@Stable
data class DashboardState(
    val groups: List<GroupTypes> = emptyList()
)
