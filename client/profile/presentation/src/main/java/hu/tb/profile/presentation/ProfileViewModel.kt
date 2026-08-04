package hu.tb.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.profile.ProfileRepository
import hu.tb.profile.domain.ProfileDeleteStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val userDatastoreRepository: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _event = Channel<ProfileEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userData = userDatastoreRepository.userdataFlow().first()
            _state.update { it.copy(username = userData.name) }
        }
    }

    fun action(action: ProfileAction) {
        when (action) {
            ProfileAction.DeleteUserClick -> deleteUser()
            ProfileAction.CloseClick -> Unit
        }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            val userId = userDatastoreRepository.userdataFlow().first().id
            val result = profileRepository.deleteProfile(userId)
            if (result == ProfileDeleteStatus.SUCCESS) {
                userDatastoreRepository.clearUserData()
                _event.send(ProfileEvent.UserDeleted)
            } else {
                _event.send(ProfileEvent.UserDeletionFailed)
            }
        }
    }
}