package hu.tb.presentation.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import hu.tb.domain.GroupTypes
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import hu.tb.ui.theme.screen_horizontal_padding
import hu.tb.ui.theme.screen_vertical_padding
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    navigationRequest: (NavigationRequest) -> Unit
) {
    DashboardScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                is DashboardAction.GroupClick -> navigationRequest(
                    DashboardAction.GroupClick(
                        groupId = it.groupId
                    )
                )

                DashboardAction.ProfileClick -> navigationRequest(DashboardAction.ProfileClick)
                DashboardAction.FindFriendClick -> navigationRequest(DashboardAction.FindFriendClick)
            }
        }
    )
}

@TraceRecomposition
@Composable
private fun DashboardScreen(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = screen_horizontal_padding, vertical = screen_vertical_padding)
        ) {
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = state.username,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { action(DashboardAction.ProfileClick) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .3f)
                    ),
                    content = {
                        Icon(
                            painter = painterResource(Icon.settings),
                            contentDescription = "profile setting icon",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                onClick = { action(DashboardAction.FindFriendClick) },
                content = {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Find your friends",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            painter = painterResource(Icon.person_search),
                            contentDescription = "person search icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                if (state.groups.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No available group to show",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.groups,
                            key = { it.groupId }
                        ) { group ->
                            when (group) {
                                is GroupTypes.Complex -> {}
                                is GroupTypes.Simple -> {
                                    Row(
                                        modifier = Modifier
                                            .clickable(
                                                onClick = {
                                                    action(
                                                        DashboardAction.GroupClick(
                                                            group.groupId
                                                        )
                                                    )
                                                }
                                            )
                                    ) {
                                        ProfileBubble(
                                            firstLetter = group.otherUsername.first().toString()
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = group.otherUsername,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileBubble(
    modifier: Modifier = Modifier,
    firstLetter: String
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = firstLetter,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    val test = List(50, init = { GroupTypes.Simple(groupId = it.toLong(), otherUsername = "abc") })
    ChatTheme {
        DashboardScreen(
            state = DashboardState(
                username = "Exmaple name",
                groups = test
            ),
            action = {}
        )
    }
}