package hu.tb.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.LoginRepository
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

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
        }
    }

    private fun profileLogin() {
        viewModelScope.launch {
            val loginInfo = LoginInfo(
                username = state.value.username.toString(),
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
                lastTokenUsed = Clock.System.now().toEpochMilliseconds()
            )
            _state.update { it.copy(isLoginHasError = false) }
            _event.send(LoginEvent.LoginSuccess)
        }
    }

    private fun serverCheck() {
        viewModelScope.launch {
            val status = loginRepository.pingServer()
            _state.update {
                it.copy(
                    serverStatus = status,
                    serverCheckedTime = Clock.System.now().toEpochMilliseconds()
                )
            }
        }
    }
}