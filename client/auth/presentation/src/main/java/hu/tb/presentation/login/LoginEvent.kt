package hu.tb.presentation.login

sealed interface LoginEvent {
    data object LoginSuccess: LoginEvent
}