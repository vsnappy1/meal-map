package com.randos.mealmap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MealMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content:
    @Composable()
    () -> Unit
) {
    MaterialTheme(
        colorScheme = getColorScheme(darkTheme, dynamicColor, LocalContext.current),
        shapes = mealMapShape,
        typography = mealMapTypography,
        content = content
    )
}
