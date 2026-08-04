package hu.tb.profile.presentation

sealed interface ProfileAction {
    data object DeleteUserClick : ProfileAction
    data object CloseClick : ProfileAction
}