package hu.tb.presentation.login

sealed interface AuthEvent {
    data object AuthSuccess: AuthEvent
}