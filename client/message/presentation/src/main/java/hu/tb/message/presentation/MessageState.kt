package hu.tb.message.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import hu.tb.message.domain.ConnectionStatus
import hu.tb.message.domain.Message

@Stable
data class MessageState(
    val userId: Long = -1,
    val otherUserName: String = "",
    val currentMessageState: TextFieldState = TextFieldState(),
    val messages: List<Message> = emptyList(),
    val isChatClosed: Boolean = false,
    val messageIdPendingDelete: Long? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTING,
    val isLoadingOlder: Boolean = false,
    // Assumed true until a page proves otherwise, so the first load is never skipped.
    val hasMoreHistory: Boolean = true
) {
    // A closed chat has no socket by design, so only a live chat can be blocked by the connection.
    val canSendMessage: Boolean
        get() = !isChatClosed && connectionStatus == ConnectionStatus.CONNECTED
}
