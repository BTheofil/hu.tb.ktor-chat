package hu.tb.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import hu.tb.domain.ServerStatus

data class LoginState(
    val username: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isLoginHasError: Boolean = false,
    val isLoading: Boolean = false,
    val serverStatus: ServerStatus = ServerStatus.DEAD,
    val serverCheckedTime: Long = 0L
)
