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
    val hasOtherUserLeft: Boolean,
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
                    otherUserName = otherUserName,
                    isChatClosed = hasOtherUserLeft
                )
            }

            getMessageHistory()
            if (!hasOtherUserLeft) {
                connectGroup()
            }
        }
    }

    fun action(action: MessageAction) =
        when (action) {
            MessageAction.SendMessage -> sendMessage()
            is MessageAction.LongPressMessage -> showDeleteDialog(action.messageId)
            MessageAction.ConfirmDeleteMessage -> deleteMessage()
            MessageAction.DismissDialog -> dismissDialog()
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

    private fun showDeleteDialog(messageId: Long) {
        _state.update { it.copy(messageIdPendingDelete = messageId) }
    }

    private fun dismissDialog() {
        _state.update { it.copy(messageIdPendingDelete = null) }
    }

    private fun deleteMessage() {
        val messageId = state.value.messageIdPendingDelete ?: return
        viewModelScope.launch {
            val isDeleted = messageRepository.deleteMessage(messageId)
            _state.update { currentState ->
                currentState.copy(
                    // The server does not broadcast deletions, so drop it locally.
                    messages = if (isDeleted) {
                        currentState.messages.filterNot { it.id == messageId }
                    } else {
                        currentState.messages
                    },
                    messageIdPendingDelete = null
                )
            }
        }
    }
}
