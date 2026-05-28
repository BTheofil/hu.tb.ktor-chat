package hu.tb.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import hu.tb.navigator.Destination
import hu.tb.navigator.Navigator
import hu.tb.ui.theme.ChatTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state = mainViewModel.state

            splashScreen.setKeepOnScreenCondition { state is MainState.Loading }

            if (state != MainState.Loading) {
                ChatTheme {
                    Navigator(
                        startDestination = when (state) {
                            MainState.HasLoggedUser -> Destination.Dashboard
                            MainState.NoLogin -> Destination.Auth
                        }
                    )
                }
            }
        }
    }
}