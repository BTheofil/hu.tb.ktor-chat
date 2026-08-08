package hu.tb.message.presentation

sealed interface MessageAction {
    data object SendMessage : MessageAction
    data class LongPressMessage(val messageId: Long) : MessageAction
    data object ConfirmDeleteMessage : MessageAction
    data object DismissDialog : MessageAction
    data object NavigateBack : MessageAction
    data object Retry : MessageAction
    data object LoadOlderMessages : MessageAction
}
