package hu.tb.message.presentation

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.message.domain.ConnectionStatus
import hu.tb.network.message.ChatConnection
import hu.tb.network.message.MessageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _event = Channel<MessageEvent>()
    val event = _event.receiveAsFlow()

    private var connection: ChatConnection? = null
    private var connectionJob: Job? = null

    init {
        viewModelScope.launch {
            val userId = userDataStore.userdataFlow().first().id
            _state.update {
                it.copy(
                    userId = userId,
                    otherUserName = otherUserName,
                    isChatClosed = hasOtherUserLeft,
                    // A closed chat never connects, so it must not look like it is connecting.
                    connectionStatus = if (hasOtherUserLeft) ConnectionStatus.DISCONNECTED
                    else ConnectionStatus.CONNECTING
                )
            }

            getMessageHistory()
            if (!hasOtherUserLeft) {
                connect()
            }
        }
    }

    override fun onCleared() {
        // The screen is gone, so the socket has to go with it.
        closeConnection()
        super.onCleared()
    }

    fun action(action: MessageAction) =
        when (action) {
            MessageAction.SendMessage -> sendMessage()
            is MessageAction.LongPressMessage -> showDeleteDialog(action.messageId)
            MessageAction.ConfirmDeleteMessage -> deleteMessage()
            MessageAction.DismissDialog -> dismissDialog()
            MessageAction.Retry -> retry()
            else -> {}
        }

    private fun retry() {
        if (state.value.isChatClosed) return
        viewModelScope.launch { connect() }
    }

    private suspend fun connect() {
        closeConnection()
        _state.update { it.copy(connectionStatus = ConnectionStatus.CONNECTING) }

        val chatConnection = messageRepository.openChatConnection(groupId)
        if (chatConnection == null) {
            _state.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
            return
        }
        connection = chatConnection
        _state.update { it.copy(connectionStatus = ConnectionStatus.CONNECTED) }

        // Collected in its own job so the socket does not keep the caller suspended forever, and
        // so a dropped connection can not take the whole ViewModel scope down with it.
        connectionJob = viewModelScope.launch {
            chatConnection.messages
                .catch { error ->
                    // Without this the failure escapes into viewModelScope and crashes the app.
                    error.printStackTrace()
                    reportDisconnected(chatConnection)
                }
                .onCompletion {
                    // The socket ended: server restart, peer close or a lost connection.
                    reportDisconnected(chatConnection)
                }
                .collect { messageData ->
                    _state.update { it.copy(messages = it.messages + messageData) }
                }
        }
    }

    /**
     * Ignores a connection that was already replaced, otherwise the old collector finishing after
     * a successful reconnect would report the new connection as dead.
     */
    private fun reportDisconnected(source: ChatConnection) {
        if (connection !== source) return
        _state.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
    }

    private fun closeConnection() {
        connectionJob?.cancel()
        connectionJob = null
        connection?.close()
        connection = null
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
        if (message.isBlank() || message.isEmpty()) return@launch

        val currentConnection = connection

        // Only drop the typed text when the message actually left the device.
        if (currentConnection?.send(message) == true) {
            _state.value.currentMessageState.clearText()
        } else {
            _state.update { it.copy(connectionStatus = ConnectionStatus.DISCONNECTED) }
            _event.send(MessageEvent.SendMessageFailed)
        }
    }

    private fun showDeleteDialog(messageId: Long) {
        _state.update { it.copy(messageIdPendingDelete = messageId) }
    }

    private fun dismissDialog() {
        _state.update { it.copy(messageIdPendingDelete = null) }
    }

    private fun deleteMessage() {
        val messageId = state.value.messageIdPendingDelete ?: return
        // Clear the pending id before the request starts, otherwise two quick taps on the
        // confirm button both pass the check above and send to delete twice.
        _state.update { it.copy(messageIdPendingDelete = null) }
        viewModelScope.launch {
            val isDeleted = messageRepository.deleteMessage(messageId)
            if (isDeleted) {
                // The delete is permanent on the server, but deletions are deliberately
                // not broadcast over the socket, so only this client drops the message.
                // Other members stop seeing it once they load the history again.
                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages.filterNot { it.id == messageId }
                    )
                }
            } else {
                _event.send(MessageEvent.DeleteMessageFailed)
            }
        }
    }
}
