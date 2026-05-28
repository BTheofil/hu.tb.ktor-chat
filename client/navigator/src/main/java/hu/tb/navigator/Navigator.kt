package hu.tb.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import hu.tb.presentation.DashboardAction
import hu.tb.presentation.DashboardScreen
import hu.tb.presentation.login.AuthScreen
import hu.tb.profile.presentation.ProfileScreen

@Stable
sealed interface Destination {
    data object Auth : Destination
    data object Dashboard : Destination
    data object Profile : Destination
}

@Composable
fun Navigator(
    startDestination: Destination
) {
    val backStack = remember { mutableStateListOf(startDestination) }

    NavDisplay(
        backStack = backStack,
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
                            is DashboardAction.GroupClick -> TODO()
                            DashboardAction.ProfileClick -> backStack.add(Destination.Profile)
                            DashboardAction.FindFriendClick -> TODO()
                        }
                    }
                )
            }
            entry<Destination.Profile> {
                ProfileScreen(
                    navigationRequest = {
                        backStack.clear()
                        backStack.add(Destination.Auth)
                    }
                )
            }
        }
    )
}