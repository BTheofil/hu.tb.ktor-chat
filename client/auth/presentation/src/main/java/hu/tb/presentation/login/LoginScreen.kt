package hu.tb.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.domain.ServerStatus
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Suppress("ParamsComparedByRef")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is LoginEvent.LoginSuccess) {
                navigationRequest()
            }
        }
    }

    LoginScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = viewModel::action
    )
}

@TraceRecomposition
@Composable
private fun LoginScreen(
    state: LoginState,
    action: (LoginAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = null
            )
    ) { innerPadding ->
        Form(
            modifier = Modifier.padding(innerPadding),
            state = state,
            action = action
        )
        Status(
            modifier = Modifier.padding(innerPadding),
            state = state,
            action = action
        )
    }

}

@TraceRecomposition
@Composable
fun Form(
    modifier: Modifier = Modifier,
    state: LoginState,
    action: (LoginAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Fill the form to login or register a profile.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = state.username,
            label = {
                Text(
                    "Username",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            enabled = !state.isLoginLoading,
            isError = state.isUsernameHasError
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = state.password,
            label = {
                Text(
                    "Password",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            enabled = !state.isLoginLoading,
            isError = state.isPasswordHasError
        )
        Spacer(Modifier.height(32.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { action(LoginAction.Enter) },
            content = {
                if (state.isLoginLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(
                            with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() }
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Enter",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            enabled = !state.isLoginLoading &&
                    state.serverStatus == ServerStatus.ALIVE
        )
    }
}

@TraceRecomposition
@Composable
fun Status(
    modifier: Modifier = Modifier,
    state: LoginState,
    action: (LoginAction) -> Unit
) {
    val ago = remember(state.serverCheckedTime) {
        LocalTime.parse(state.serverCheckedTime)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { action(LoginAction.ServerCheck) },
                    indication = null,
                    interactionSource = null
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isServerCheckLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Icon.database),
                    contentDescription = "server state icon",
                    tint = when (state.serverStatus) {
                        ServerStatus.ALIVE -> MaterialTheme.colorScheme.primary
                        ServerStatus.DEAD -> MaterialTheme.colorScheme.error
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Last time checked: $ago",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview(apiLevel = 36)
@Composable
private fun LoginScreenPreview() {
    ChatTheme {
        LoginScreen(
            state = LoginState(
                serverStatus = ServerStatus.ALIVE
            ),
            action = {}
        )
    }
}