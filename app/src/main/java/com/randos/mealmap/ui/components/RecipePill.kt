package com.randos.mealmap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.mealmap.utils.vibrateOnClick

@Composable
fun <T> RecipePill(
    item: T,
    onItemSelect: (T) -> Unit,
    isSelected: Boolean,
    displayValue: (T) -> String
) {
    val context = LocalContext.current
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val cardContainerColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    Card(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onItemSelect(item)
                context.vibrateOnClick()
            },
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            text = displayValue(item),
            style = MaterialTheme.typography.labelLarge.copy(color = textColor)
        )
    }
}

@Preview
@Composable
private fun RecipePillPreview(){
    RecipePill(item = "Breakfast", onItemSelect = {}, isSelected = true, displayValue = {it})
}