package hu.tb.message.presentation

sealed interface MessageAction {
    data object SendMessage : MessageAction
    data object DeleteMessage : MessageAction
}