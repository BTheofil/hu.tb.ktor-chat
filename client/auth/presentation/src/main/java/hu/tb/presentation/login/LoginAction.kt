package hu.tb.presentation.login

sealed interface LoginAction {
    data object Enter: LoginAction
    data object ServerCheck: LoginAction
}