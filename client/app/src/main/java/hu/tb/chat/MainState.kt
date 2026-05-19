package hu.tb.chat

sealed interface MainState {
    data object Loading : MainState
    data object HasLoggedUser : MainState
    data object NoLogin : MainState
}