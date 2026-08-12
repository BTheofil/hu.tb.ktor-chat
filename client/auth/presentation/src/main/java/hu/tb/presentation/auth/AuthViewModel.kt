package hu.tb.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import hu.tb.domain.LoginResult
import hu.tb.network.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _event = Channel<AuthEvent>()
    val event = _event.receiveAsFlow()

    init {
        serverCheck()
    }

    fun action(action: AuthAction) {
        when (action) {
            AuthAction.Enter -> profileLogin()
            AuthAction.ServerCheck -> serverCheck()
            AuthAction.TogglePasswordVisibility -> togglePasswordVisibility()
            AuthAction.CloseDuplicatedNameDialog -> closeDuplicatedDialog()
        }
    }

    private fun closeDuplicatedDialog() {
        _state.update { it.copy(isUserDuplicated = false) }
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
            _state.update { it.copy(isLoginLoading = true, isLoginHasError = false) }

            val loginInfo = LoginInfo(
                username = state.value.username.text.toString(),
                password = state.value.password.text.toString()
            )

            when (val result = authRepository.handleLogin(loginInfo)) {
                LoginResult.Failure ->
                    _state.update { it.copy(isLoginHasError = true, isLoginLoading = false) }

                LoginResult.UsernameTaken ->
                    _state.update { it.copy(isUserDuplicated = true, isLoginLoading = false) }

                is LoginResult.Success -> {
                    userDatastoreRepository.updateUserData(
                        id = result.userInfo.userId,
                        name = state.value.username.text.toString(),
                        password = state.value.password.text.toString(),
                        token = result.userInfo.token,
                        tokenRefreshDate = LocalDateTime.now().toString()
                    )

                    _state.update { it.copy(isLoginLoading = false) }
                    _event.send(AuthEvent.AuthSuccess)
                }
            }
        }
    }

    private fun serverCheck() {
        viewModelScope.launch {
            _state.update { it.copy(isServerCheckLoading = true) }
            delay(5.milliseconds)
            val status = authRepository.pingServer()
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