package hu.tb.presentation.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginState(
    val username: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState()
)
