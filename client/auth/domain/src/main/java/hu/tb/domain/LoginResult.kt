package hu.tb.domain

sealed interface LoginResult {
    data class Success(val userInfo: UserInfo, val isNewAccount: Boolean) : LoginResult
    data object UsernameTaken : LoginResult
    data object Failure : LoginResult
}
