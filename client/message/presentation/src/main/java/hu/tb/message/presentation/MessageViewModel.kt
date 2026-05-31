package hu.tb.message.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageViewModel : ViewModel() {

    private val _state = MutableStateFlow(MessageState())
    val state = _state.asStateFlow()

    fun action(action: MessageAction) {
        when (action) {
            MessageAction.DeleteMessage -> TODO()
            MessageAction.SendMessage -> sendMessage()
        }
    }

    private fun sendMessage() {

    }
}