package hu.tb.profile.presentation

sealed interface ProfileAction {
    data object DeleteUserClick : ProfileAction
}