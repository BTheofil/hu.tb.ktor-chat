package hu.tb.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.LoginRepository
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEnter() {
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
                token = token.value
            )
            _state.update { it.copy(isLoginHasError = false) }
        }
    }
}