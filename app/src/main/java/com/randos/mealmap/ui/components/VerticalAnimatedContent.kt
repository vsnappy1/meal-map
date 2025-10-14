package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> VerticalAnimatedContent(
    targetState: T,
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        modifier = modifier.fillMaxWidth(),
        targetState = targetState,
        label = label,
        contentAlignment = Alignment.TopCenter,
        transitionSpec = {
            slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn() togetherWith
                slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
        }
    ) { state ->
        content(state)
    }
}
