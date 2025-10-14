package com.randos.mealmap.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.ContextClick
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> RecipePill(item: T, onItemSelect: (T) -> Unit, isSelected: Boolean, displayValue: (T) -> String) {
    val haptics = LocalHapticFeedback.current

    val borderColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "borderColorAnim"
    )
    val textColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "textColorAnim"
    )
    val cardContainerColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.background
        },
        label = "cardBgAnim"
    )

    Card(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onItemSelect(item)
                haptics.performHapticFeedback(ContextClick)
            },
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        shape = CircleShape
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
            text = displayValue(item),
            style = MaterialTheme.typography.labelLarge.copy(color = textColor)
        )
    }
}

@Preview
@Composable
private fun RecipePillPreview() {
    RecipePill(item = "Breakfast", onItemSelect = {}, isSelected = true, displayValue = { it })
}

@Preview
@Composable
private fun RecipePillPreviewNotSelected() {
    RecipePill(item = "Breakfast", onItemSelect = {}, isSelected = false, displayValue = { it })
}
