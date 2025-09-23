package com.randos.mealmap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.utils.Utils

@Composable
fun RecipeUnitDropDown(
    modifier: Modifier = Modifier,
    onUnitChange: (IngredientUnit) -> Unit,
    ingredientUnit: IngredientUnit?
) {
    var isExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .clickable { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .weight(1f),
                text = ingredientUnit?.value ?: "Unit",
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            Utils.ingredientUnits.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.value) },
                    onClick = {
                        isExpanded = false
                        onUnitChange(it)
                    })
            }
        }
    }
}

@Preview
@Composable
fun RecipeUnitDropDownPreview() {
    RecipeUnitDropDown(
        onUnitChange = {},
        ingredientUnit = IngredientUnit.GRAM
    )
}
