package hu.tb.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
private fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("hello")
    }
}

@Preview(apiLevel = 36)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}