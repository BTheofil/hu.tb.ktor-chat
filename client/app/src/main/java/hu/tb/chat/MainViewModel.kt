package hu.tb.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.LoginRepository
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val loginRepository: LoginRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    var state by mutableStateOf<MainState>(MainState.Loading)
        private set

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            if (userData.token.isEmpty()) {
                state = MainState.NoLogin
                return@launch
            }

            val newToken = loginRepository.autoLogin(LoginInfo(userData.name, userData.password))
            if (newToken == null) {
                state = MainState.NoLogin
                return@launch
            } else {
                userDatastoreRepository.updateUserData(
                    token = newToken.value,
                    tokenRefreshDate = LocalDateTime.now().toString()
                )
                state = MainState.HasLoggedUser
            }
        }
    }
}