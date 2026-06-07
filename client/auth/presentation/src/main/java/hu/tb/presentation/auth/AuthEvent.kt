package hu.tb.presentation.auth

sealed interface AuthEvent {
    data object AuthSuccess: AuthEvent
}