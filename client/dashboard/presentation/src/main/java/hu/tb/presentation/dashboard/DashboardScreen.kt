package hu.tb.presentation.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.domain.Group
import hu.tb.ui.component.ConfirmDialog
import hu.tb.ui.modifier.clearFocus
import hu.tb.ui.theme.ChatTheme
import hu.tb.ui.theme.Icon
import hu.tb.ui.theme.screen_horizontal_padding
import hu.tb.ui.theme.screen_vertical_padding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    navigationRequest: (NavigationRequest) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            val message = when (event) {
                DashboardEvent.AddFriendFailed -> "Could not add friend, try again."
                DashboardEvent.LeaveGroupFailed -> "Could not leave the chat, try again."
                DashboardEvent.SearchFailed -> "Search failed, check your connection."
            }
            snackbarHostState.showSnackbar(message = message)
        }
    }

    DashboardScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                is DashboardAction.GroupClick -> navigationRequest(
                    DashboardAction.GroupClick(
                        groupId = it.groupId,
                        otherUserName = it.otherUserName,
                        hasOtherUserLeft = it.hasOtherUserLeft
                    )
                )

                DashboardAction.ProfileClick -> navigationRequest(DashboardAction.ProfileClick)
                else -> viewModel.action(it)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@TraceRecomposition
@Composable
private fun DashboardScreen(
    snackbarHostState: SnackbarHostState,
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .clearFocus(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
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
            SearchWidget(state, action)
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
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .combinedClickable(
                                        onClick = {
                                            action(
                                                DashboardAction.GroupClick(
                                                    groupId = group.groupId,
                                                    otherUserName = group.otherUsername,
                                                    hasOtherUserLeft = group.hasOtherUserLeft
                                                )
                                            )
                                        },
                                        onLongClick = {
                                            action(DashboardAction.LongPressGroup(group.groupId))
                                        }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileBubble(
                                    firstLetter = group.otherUsername.firstOrNull()?.toString()
                                        ?: CLOSED_CHAT_INITIAL
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = if (group.hasOtherUserLeft) CLOSED_CHAT_LABEL
                                    else group.otherUsername,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (group.hasOtherUserLeft) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = .6f)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.groupIdPendingLeave != null) {
        ConfirmDialog(
            title = "Leave chat",
            text = "You will no longer see this chat. You have to add each other again to send messages.",
            confirmLabel = "Leave",
            onConfirm = { action(DashboardAction.ConfirmLeaveGroup) },
            onDismiss = { action(DashboardAction.DismissDialog) }
        )
    }
}

private const val CLOSED_CHAT_LABEL = "Chat closed"
private const val CLOSED_CHAT_INITIAL = "-"

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
fun SearchWidget(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val searchInputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = state.searchText,
            searchBarState = searchBarState,
            onSearch = {
                action(DashboardAction.Search)
            },
            placeholder = {
                Text(
                    text = "Find your friends",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            leadingIcon = {
                AnimatedContent(
                    targetState = searchBarState.currentValue,
                ) { searchStateValue ->
                    when (searchStateValue) {
                        SearchBarValue.Collapsed -> Icon(
                            painter = painterResource(Icon.person_search),
                            contentDescription = "person search icon",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        SearchBarValue.Expanded -> Icon(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        state.searchText.clearText()
                                        scope.launch { searchBarState.animateToCollapsed() }
                                    }
                                ),
                            painter = painterResource(Icon.arrow_left),
                            contentDescription = "person search icon",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        state = searchBarState,
        inputField = searchInputField,
    )
    ExpandedFullScreenSearchBar(
        modifier = Modifier
            .fillMaxWidth(),
        state = searchBarState,
        inputField = searchInputField,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        content = {
            if (state.isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = screen_vertical_padding,
                            horizontal = screen_horizontal_padding
                        )
                ) {
                    items(
                        items = state.searchResults
                    ) { user ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = user.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                modifier = Modifier
                                    .clickable(
                                        enabled = !user.isFriend,
                                        onClick = {
                                            action(DashboardAction.MakeFriend(user.id))
                                            state.searchText.clearText()
                                            scope.launch { searchBarState.animateToCollapsed() }
                                        }
                                    ),
                                painter = painterResource(if (user.isFriend) Icon.check_circle else Icon.group_add),
                                contentDescription = "is user already in group icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    )
}

@TraceRecomposition
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
    val test = List(50, init = { Group(groupId = it.toLong(), otherUsername = "abc") })
    ChatTheme {
        DashboardScreen(
            snackbarHostState = SnackbarHostState(),
            state = DashboardState(
                username = "Example name",
                groups = test
            ),
            action = {}
        )
    }
}