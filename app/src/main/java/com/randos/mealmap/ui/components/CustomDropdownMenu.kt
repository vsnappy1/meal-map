package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> CustomDropdownMenu(
    modifier: Modifier = Modifier,
    value: T?,
    onValueChange: (T) -> Unit,
    hint: String,
    items: List<T>,
    getTextValue: (T) -> String
) {
    Spacer(modifier = Modifier.height(if (value != null) 4.dp else 8.dp))
    AnimatedVisibility(
        visible = value != null,
        enter = fadeIn() + expandVertically(),
    ) {
        Text(modifier = Modifier.padding(bottom = 4.dp), text = hint)
    }
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        onClick = { isExpanded = !isExpanded },
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (value == null) hint else getTextValue(value),
                textAlign = TextAlign.Center
            )
            Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null)
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                items.forEach {
                    DropdownMenuItem(
                        text = { Text(text = getTextValue(it)) },
                        onClick = {
                            onValueChange(it)
                            isExpanded = false
                        })
                }
            }
        }
    }
}

@Preview
@Composable
private fun CustomDropdownMenuPreview() {
    MaterialTheme {
        CustomDropdownMenu(
            modifier = Modifier,
            value = null,
            onValueChange = {},
            hint = "Select a category",
            items = listOf("Breakfast", "Lunch", "Dinner", "Snack"),
            getTextValue = { it }
        )
    }
}
