package hu.tb.message.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.message.domain.ConnectionStatus
import hu.tb.message.domain.Message
import hu.tb.message.presentation.components.MessageBubble
import hu.tb.ui.component.ConfirmDialog
import hu.tb.ui.modifier.clearFocus
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageScreen(
    viewModel: MessageViewModel,
    navigationRequest: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            val message = when (event) {
                MessageEvent.SendMessageFailed -> "Message not sent, you are offline."
                MessageEvent.DeleteMessageFailed -> "Could not delete the message, try again."
            }
            snackbarHostState.showSnackbar(message = message)
        }
    }

    MessageScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                MessageAction.NavigateBack -> navigationRequest()
                else -> viewModel.action(it)
            }
        }
    )
}

@TraceRecomposition
@Composable
private fun MessageScreen(
    snackbarHostState: SnackbarHostState,
    state: MessageState,
    action: (MessageAction) -> Unit
) {
    val listState = rememberLazyListState()
    val groupedMessages = remember(state.messages.size) {
        state.messages.groupBy { it.timeStamp.toLocalDate() }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            MessageBar(
                title = if (state.isChatClosed) "Chat closed"
                else "Chat with ${state.otherUserName}",
                navigateBack = { action(MessageAction.NavigateBack) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            ConnectionBar(
                connectionStatus = state.connectionStatus,
                retry = { action(MessageAction.Retry) }
            )
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .clearFocus()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Bottom),
            ) {
                groupedMessages.forEach { (date, messages) ->
                    item(
                        content = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .animateItem(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(Modifier.weight(1f))
                                Text(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    text = date.format(DateTimeFormatter.ofPattern("yyyy. MMMM d.")),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = .7f)
                                )
                                HorizontalDivider(Modifier.weight(1f))
                            }
                        }
                    )
                    items(
                        items = messages,
                    ) { message ->
                        val isSelfMessage = state.userId == message.senderId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            contentAlignment = if (isSelfMessage) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            val formattedMinute =
                                if (message.timeStamp.minute < 10) "0${message.timeStamp.minute}" else message.timeStamp.minute
                            MessageBubble(
                                content = message.content,
                                messageTimeSent = "${message.timeStamp.hour}:$formattedMinute",
                                onLongClick = if (isSelfMessage) {
                                    { action(MessageAction.LongPressMessage(message.id)) }
                                } else null
                            )
                        }
                    }
                }
                if (state.isChatClosed) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = "The other user left the chat",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = .7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            if (!state.isChatClosed) {
                Spacer(Modifier.height(16.dp))
                MessageControl(
                    textState = state.currentMessageState,
                    isSendEnabled = state.canSendMessage,
                    sendClick = { action(MessageAction.SendMessage) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (state.messageIdPendingDelete != null) {
        ConfirmDialog(
            title = "Delete message",
            text = "This message will be deleted. Others may still see it until they reopen the chat.",
            confirmLabel = "Delete",
            onConfirm = { action(MessageAction.ConfirmDeleteMessage) },
            onDismiss = { action(MessageAction.DismissDialog) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageBar(
    modifier: Modifier = Modifier,
    title: String,
    navigateBack: () -> Unit
) {
    var isBackEnabled by remember { mutableStateOf(true) }

    TopAppBar(
        modifier = modifier
            .fillMaxWidth(),
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navigateBack()
                    isBackEnabled = false
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent
                ),
                content = {
                    Icon(
                        painterResource(Icon.arrow_left),
                        contentDescription = "back navigation icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                enabled = isBackEnabled
            )
        }
    )
}

@TraceRecomposition
@Composable
private fun ConnectionBar(
    connectionStatus: ConnectionStatus,
    retry: () -> Unit
) {
    // Retrying flips the status to CONNECTING and this button only exists while DISCONNECTED, but
    // composition catches up a frame later, so a second tap in the same frame would still reach
    // this lambda and open a rival connection. Checked inside the lambda on purpose: an `enabled`
    // flag would need that same missing recomposition to take effect.
    var isRetryTapped by remember { mutableStateOf(false) }
    LaunchedEffect(connectionStatus) { isRetryTapped = false }

    val containerColor = when (connectionStatus) {
        ConnectionStatus.DISCONNECTED -> MaterialTheme.colorScheme.errorContainer
        ConnectionStatus.CONNECTING -> MaterialTheme.colorScheme.secondaryContainer
        ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = when (connectionStatus) {
        ConnectionStatus.DISCONNECTED -> MaterialTheme.colorScheme.onErrorContainer
        ConnectionStatus.CONNECTING -> MaterialTheme.colorScheme.onSecondaryContainer
        ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    AnimatedVisibility(
        visible = connectionStatus != ConnectionStatus.CONNECTED,
        enter = fadeIn(),
        exit = fadeOut(tween(durationMillis = 1000, easing = LinearEasing))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = containerColor)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = connectionStatus.label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
            if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                TextButton(
                    onClick = {
                        if (!isRetryTapped) {
                            isRetryTapped = true
                            retry()
                        }
                    }
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageControl(
    textState: TextFieldState,
    isSendEnabled: Boolean,
    sendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                state = textState,
                enabled = isSendEnabled
            )
        }
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = if (isSendEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable(enabled = isSendEnabled, onClick = sendClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(22.dp),
                painter = painterResource(Icon.send),
                contentDescription = "send icon",
                tint = if (isSendEnabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun MessageScreenPreview() {
    ChatTheme {
        MessageScreen(
            snackbarHostState = remember { SnackbarHostState() },
            state = MessageState(
                userId = 2,
                otherUserName = "Other_user",
                connectionStatus = ConnectionStatus.CONNECTED,
                messages = listOf(
                    Message(
                        id = 1,
                        senderId = 1,
                        content = "test message",
                        timeStamp = LocalDateTime.of(2026, 6, 22, 1, 0)
                    ),
                    Message(
                        id = 2,
                        senderId = 2,
                        content = "other message",
                        timeStamp = LocalDateTime.of(2026, 6, 22, 1, 1)
                    )
                )
            ),
            action = {}
        )
    }
}