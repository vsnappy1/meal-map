package com.randos.mealmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Ingredient
import kotlin.math.min

@Composable
fun RecipeIngredientSuggestion(
    modifier: Modifier = Modifier,
    suggestions: List<Ingredient>,
    onSuggestionItemSelected: (Ingredient) -> Unit,
    onDeleteSuggestion: (Ingredient) -> Unit
) {
    val itemHeight = 50.dp // Standard DropdownMenuItem height
    val maxVisibleItems = 3
    val dropdownHeight = itemHeight * min(suggestions.size, maxVisibleItems)
    if (suggestions.isNotEmpty()) {
        Column(
            modifier = modifier
                .height(dropdownHeight)
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            suggestions.forEachIndexed { index, ingredient ->
                DropdownMenuItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = ingredient.name
                            )
                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {
                                    onDeleteSuggestion(ingredient)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    onClick = { onSuggestionItemSelected(ingredient) })
                if (index < suggestions.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecipeIngredientSuggestionPreview() {
    RecipeIngredientSuggestion(
        suggestions = listOf(
            Ingredient(name = "Apples"),
            Ingredient(name = "Bananas"),
            Ingredient(name = "Cherries"),
            Ingredient(name = "Dates"),
            Ingredient(name = "Elderberries"),
        ),
        onSuggestionItemSelected = {},
        onDeleteSuggestion = {}
    )
}


