package hu.tb.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import hu.tb.message.presentation.MessageScreen
import hu.tb.message.presentation.MessageViewModel
import hu.tb.presentation.dashboard.DashboardAction
import hu.tb.presentation.dashboard.DashboardScreen
import hu.tb.presentation.auth.AuthScreen
import hu.tb.profile.presentation.ProfileScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Stable
sealed interface Destination {
    data object Auth : Destination
    data object Dashboard : Destination
    data object Profile : Destination
    data class Message(
        val groupId: Long,
        val otherUserName: String,
        val hasOtherUserLeft: Boolean
    ) : Destination
}

@Composable
fun Navigator(
    startDestination: Destination
) {
    val backStack = remember { mutableStateListOf(startDestination) }

    NavDisplay(
        backStack = backStack,
        // Without a ViewModelStore decorator every screen resolves its ViewModel against the
        // activity, so a popped entry never clears its ViewModel and a second chat would reuse
        // the first one. The saveable state holder is the framework default and must be kept.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Destination.Auth> {
                AuthScreen(
                    navigationRequest = {
                        backStack.add(Destination.Dashboard)
                        backStack.remove(Destination.Auth)
                    }
                )
            }
            entry<Destination.Dashboard> {
                DashboardScreen(
                    navigationRequest = {
                        when (it) {
                            is DashboardAction.GroupClick -> backStack.add(
                                Destination.Message(
                                    groupId = it.groupId,
                                    otherUserName = it.otherUserName,
                                    hasOtherUserLeft = it.hasOtherUserLeft
                                )
                            )

                            DashboardAction.ProfileClick -> backStack.add(Destination.Profile)
                        }
                    }
                )
            }
            entry<Destination.Profile> {
                ProfileScreen(
                    navigateBack = {
                        backStack.removeAt(backStack.lastIndex)
                    },
                    navigationRequest = {
                        backStack.clear()
                        backStack.add(Destination.Auth)
                    }
                )
            }
            entry<Destination.Message> { key ->
                val viewModel = koinViewModel<MessageViewModel> {
                    parametersOf(key.groupId, key.otherUserName, key.hasOtherUserLeft)
                }
                MessageScreen(
                    viewModel = viewModel,
                    navigationRequest = {
                        backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
        }
    )
}