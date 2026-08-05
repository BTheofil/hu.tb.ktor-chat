package hu.tb.profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.ui.component.ConfirmDialog
import hu.tb.ui.component.InitialAvatar
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import hu.tb.ui.theme.screen_horizontal_padding
import hu.tb.ui.theme.screen_vertical_padding
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    navigateBack: () -> Unit,
    navigationRequest: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                // Both end the session, and both need the same clean start on the auth screen.
                ProfileEvent.UserDeleted,
                ProfileEvent.LoggedOut -> navigationRequest()

                ProfileEvent.UserDeletionFailed -> {
                    snackbarHostState.showSnackbar(message = "Currently can not delete profile.")
                }
            }
        }
    }

    ProfileScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                ProfileAction.CloseClick -> navigateBack()
                else -> viewModel.action(it)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { action(ProfileAction.CloseClick) },
                        content = {
                            Icon(
                                painter = painterResource(Icon.close),
                                contentDescription = "close icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                },
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = screen_horizontal_padding,
                    vertical = screen_vertical_padding
                )
        ) {
            ProfileHeader(username = state.username)
            Spacer(Modifier.height(24.dp))
            ProfileSection(title = "Account") {
                ProfileRow(
                    label = "Log out",
                    description = "Sign out on this device.",
                    onClick = { action(ProfileAction.LogoutClick) }
                )
            }
            Spacer(Modifier.height(16.dp))
            ProfileSection(
                title = "Danger zone",
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                ProfileRow(
                    label = "Delete profile",
                    description = "Removes your account for good.",
                    labelColor = MaterialTheme.colorScheme.error,
                    onClick = { action(ProfileAction.DeleteUserClick) }
                )
            }
        }
    }

    if (state.isDeleteDialogVisible) {
        ConfirmDialog(
            title = "Delete profile",
            text = "Your account and your chats are deleted. This can not be undone.",
            confirmLabel = "Delete",
            onConfirm = { action(ProfileAction.ConfirmDeleteUser) },
            onDismiss = { action(ProfileAction.DismissDialog) }
        )
    }
}

@Composable
private fun ProfileHeader(username: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InitialAvatar(
            firstLetter = username.take(1).uppercase(),
            size = 88.dp,
            textStyle = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = username,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit
) {
    Text(
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        content = { content() }
    )
}

@Composable
private fun ProfileRow(
    label: String,
    description: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = labelColor
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
