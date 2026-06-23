package hu.tb.message.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import hu.tb.message.domain.Message

@Stable
data class MessageState(
    val userId: Long = -1,
    val otherUserName: String = "",
    val currentMessageState: TextFieldState = TextFieldState(),
    val messages: List<Message> = emptyList()
)
