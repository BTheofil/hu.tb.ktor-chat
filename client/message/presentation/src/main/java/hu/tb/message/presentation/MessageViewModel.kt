package hu.tb.message.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.message.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MessageState())
    val state = _state.asStateFlow()

    fun action(action: MessageAction) {
        when (action) {
            MessageAction.DeleteMessage -> TODO()
            MessageAction.SendMessage -> sendMessage()
        }
    }

    fun connectGroup(groupId: Long) {
        viewModelScope.launch {
            messageRepository.sendMessage(groupId)
        }
    }

    private fun sendMessage() {

    }
}