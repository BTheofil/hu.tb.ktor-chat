package hu.tb.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.screen_horizontal_padding
import hu.tb.ui.theme.screen_vertical_padding
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ProfileEvent.UserDeleted -> navigationRequest()
                ProfileEvent.UserDeletionFailed -> {
                    snackbarHostState.showSnackbar(message = "Currently can not delete profile.")
                }
            }
        }
    }

    ProfileScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = viewModel::action
    )
}

@TraceRecomposition
@Composable
private fun ProfileScreen(
    snackbarHostState: SnackbarHostState,
    state: ProfileState,
    action: (ProfileAction) -> Unit
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = screen_horizontal_padding,
                    vertical = screen_vertical_padding
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.username,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { action(ProfileAction.DeleteUserClick) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    content = {
                        Text(
                            text = "Delete profile",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ChatTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(username = "Example username"),
            action = {}
        )
    }
}