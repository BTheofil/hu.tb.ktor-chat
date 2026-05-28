package hu.tb.presentation.login

sealed interface AuthAction {
    data object Enter: AuthAction
    data object ServerCheck: AuthAction
    data object TogglePasswordVisibility: AuthAction
}