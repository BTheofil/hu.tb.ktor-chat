package hu.tb.profile.presentation

sealed interface ProfileEvent {
    data object UserDeleted : ProfileEvent
    data object UserDeletionFailed : ProfileEvent
    data object LoggedOut : ProfileEvent
}
