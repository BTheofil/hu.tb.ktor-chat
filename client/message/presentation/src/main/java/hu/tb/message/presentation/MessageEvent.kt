package hu.tb.message.presentation

sealed interface MessageEvent {
    data object SendMessageFailed : MessageEvent
    data object DeleteMessageFailed : MessageEvent
}
