package hu.tb.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.LoginRepository
import hu.tb.domain.LoginInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEnter() {
        val token = viewModelScope.async {
            val loginInfo = LoginInfo(
                username = state.value.username.toString(),
                password = state.value.password.text.toString()
            )
            loginRepository.handleLogin(loginInfo)
        }

        if (token.getCompleted() == null) {
            _state.update { it.copy(isLoginHasError = true) }
            return
        }

    }
}