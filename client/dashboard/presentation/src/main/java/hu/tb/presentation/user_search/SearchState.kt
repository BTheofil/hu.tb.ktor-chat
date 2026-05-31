package hu.tb.presentation.user_search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import hu.tb.domain.UserMatch

@Stable
data class SearchState(
    val searchText: TextFieldState = TextFieldState(),
    val matches: List<UserMatch> = emptyList()
)
