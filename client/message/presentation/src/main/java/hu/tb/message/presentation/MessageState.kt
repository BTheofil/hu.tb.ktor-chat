package hu.tb.message.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable

@Stable
data class MessageState(
    val currentMessageState: TextFieldState = TextFieldState(),
    val messages: List<String> = emptyList()
)
