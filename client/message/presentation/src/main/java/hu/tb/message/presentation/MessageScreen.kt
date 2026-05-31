package hu.tb.message.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import org.koin.androidx.compose.koinViewModel

@Composable
fun MessageScreen(
    viewModel: MessageViewModel = koinViewModel(),
) {
    MessageScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = viewModel::action
    )
}

@TraceRecomposition
@Composable
private fun MessageScreen(
    state: MessageState,
    action: (MessageAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(
                    items = state.messages,
                ) { message ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = message,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            MessageControl(
                textState = state.currentMessageState,
                sendClick = { action(MessageAction.SendMessage) }
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun MessageControl(
    textState: TextFieldState,
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
                state = textState
            )
        }
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .clickable(
                    onClick = sendClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(24.dp),
                painter = painterResource(Icon.send),
                contentDescription = "send icon",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun MessageScreenPreview() {
    ChatTheme {
        MessageScreen(
            state = MessageState(),
            action = {}
        )
    }
}