package hu.tb.profile.presentation

sealed interface ProfileAction {
    data object DeleteUserClick : ProfileAction
    data object ConfirmDeleteUser : ProfileAction
    data object DismissDialog : ProfileAction
    data object LogoutClick : ProfileAction
    data object CloseClick : ProfileAction
}
