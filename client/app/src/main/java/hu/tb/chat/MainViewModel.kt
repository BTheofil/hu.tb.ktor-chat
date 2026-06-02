package hu.tb.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.domain.LoginInfo
import hu.tb.network.auth.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val authRepository: AuthRepository,
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

            val userInfo = authRepository.autoLogin(LoginInfo(userData.name, userData.password))
            if (userInfo == null) {
                state = MainState.NoLogin
                return@launch
            } else {
                userDatastoreRepository.updateUserData(
                    token = userInfo.token,
                    tokenRefreshDate = LocalDateTime.now().toString()
                )
                state = MainState.HasLoggedUser
            }
        }
    }
}