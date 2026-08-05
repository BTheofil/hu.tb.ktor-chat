package hu.tb.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InitialAvatar(
    firstLetter: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = firstLetter,
            style = textStyle,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
