package hu.tb.presentation.auth

sealed interface AuthEvent {
    data class AuthSuccess(val isNewAccount: Boolean): AuthEvent
}