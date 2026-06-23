package hu.tb.message.presentation

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.message.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    val groupId: Long,
    val otherUserName: String,
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDatastoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MessageState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userDataStore.userdataFlow().first().id
            _state.update {
                it.copy(
                    userId = userId,
                    otherUserName = otherUserName
                )
            }

            getMessageHistory()
            connectGroup()
        }
    }

    fun action(action: MessageAction) =
        when (action) {
            MessageAction.DeleteMessage -> TODO()
            MessageAction.SendMessage -> sendMessage()
            else -> {}
        }

    private suspend fun connectGroup() =
        messageRepository.connectGroupObserver(groupId)
            ?.collectLatest { messageData ->
                _state.update {
                    it.copy(
                        messages = state.value.messages + messageData
                    )
                }
            }

    private suspend fun getMessageHistory() {
        val messages = messageRepository.getMessageHistory(groupId.toInt())
        if (messages != null) {
            _state.update {
                it.copy(
                    messages = messages
                )
            }
        }
    }

    private fun sendMessage() = viewModelScope.launch {
        val message = state.value.currentMessageState.text.toString()
        messageRepository.sendMessage(message = message)

        _state.value.currentMessageState.clearText()
    }

}