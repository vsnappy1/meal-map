package com.randos.mealmap.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.randos.mealmap.ui.components.ScreenHeadingText

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ScreenHeadingText(text = "Settings")
    }
}