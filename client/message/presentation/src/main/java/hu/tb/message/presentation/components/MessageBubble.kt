package hu.tb.message.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.ui.theme.ChatTheme

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    content: String,
    messageTimeSent: String
) {
    var isExpanded by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = content,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                (fadeIn() + slideInHorizontally())
                    .togetherWith(
                        exit = (fadeOut() + slideOutHorizontally())
                    )
            }
        ) { isExpanded ->
            if (isExpanded) {
                Row {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = messageTimeSent,
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

            }
        }
    }
}

@TraceRecomposition
@Preview
@Composable
private fun MessageBubblePreview() {
    ChatTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MessageBubble(
                content = "Example text",
                messageTimeSent = "16:44"
            )
        }
    }
}