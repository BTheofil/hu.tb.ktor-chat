package hu.tb.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import hu.tb.domain.ServerStatus

data class LoginState(
    val username: TextFieldState = TextFieldState(),
    val isUsernameHasError: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isPasswordHasError: Boolean = false,
    val isLoginHasError: Boolean = false,
    val isLoginLoading: Boolean = false,
    val serverStatus: ServerStatus = ServerStatus.DEAD,
    val serverCheckedTime: String = "00:00",
    val isServerCheckLoading: Boolean = false
)
