package hu.tb.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.LoginRepository
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    init {
        serverCheck()
    }

    fun action(action: LoginAction) {
        when (action) {
            LoginAction.Enter -> profileLogin()
            LoginAction.ServerCheck -> serverCheck()
            LoginAction.TogglePasswordVisibility -> togglePasswordVisibility()
        }
    }

    private fun profileLogin() {
        _state.update {
            it.copy(
                isUsernameHasError = state.value.username.text.isEmpty(),
                isPasswordHasError = state.value.password.text.isEmpty()
            )
        }
        if (state.value.isUsernameHasError || state.value.isPasswordHasError) return

        viewModelScope.launch {
            val loginInfo = LoginInfo(
                username = state.value.username.text.toString(),
                password = state.value.password.text.toString()
            )
            val token = loginRepository.handleLogin(loginInfo)

            if (token == null) {
                _state.update { it.copy(isLoginHasError = true) }
                return@launch
            }

            userDatastoreRepository.updateUserData(
                name = state.value.username.toString(),
                password = state.value.password.text.toString(),
                token = token.value,
                lastTokenUsed = LocalTime.now().toString()
            )
            _state.update { it.copy(isLoginHasError = false) }
            _event.send(LoginEvent.LoginSuccess)
        }
    }

    private fun serverCheck() {
        viewModelScope.launch {
            _state.update { it.copy(isServerCheckLoading = true) }
            delay(5.milliseconds)
            val status = loginRepository.pingServer()
            _state.update {
                it.copy(
                    serverStatus = status,
                    serverCheckedTime = LocalTime.now().toString(),
                    isServerCheckLoading = false
                )
            }
        }
    }

    private fun togglePasswordVisibility() {
        _state.update {
            it.copy(
                isPasswordVisible = !state.value.isPasswordVisible
            )
        }
    }
}