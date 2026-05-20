package hu.tb.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import hu.tb.dashboard.DashboardScreen
import hu.tb.presentation.login.LoginScreen

@Stable
sealed interface Destination {
    data object Login : Destination
    data object Dashboard : Destination
}

@Composable
fun Navigator(
    startDestination: Destination
) {
    val backStack = remember { mutableStateListOf(startDestination) }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Destination.Login> {
                LoginScreen(
                    navigationRequest = {
                        backStack.add(Destination.Dashboard)
                        backStack.remove(Destination.Login)
                    }
                )
            }
            entry<Destination.Dashboard> {
                DashboardScreen()
            }
        }
    )
}