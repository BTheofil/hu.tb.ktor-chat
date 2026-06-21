package hu.tb.message.presentation

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.message.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    val groupId: Long,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MessageState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            messageRepository.connectGroupObserver(groupId)
                ?.collectLatest { messageData ->
                    _state.update {
                        it.copy(
                            messages = state.value.messages + messageData
                        )
                    }
                }
        }
    }

    fun action(action: MessageAction) {
        when (action) {
            MessageAction.DeleteMessage -> TODO()
            MessageAction.SendMessage -> sendMessage()
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {
            val message = state.value.currentMessageState.text.toString()
            messageRepository.sendMessage(message = message)

            _state.value.currentMessageState.clearText()
        }
    }
}