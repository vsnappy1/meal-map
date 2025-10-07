package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Recipe
import com.randos.mealmap.utils.Utils
import kotlin.math.min

@Composable
fun RecipeSuggestion(
    modifier: Modifier = Modifier,
    suggestions: List<Recipe>,
    recipeName: String,
    onRecipeAddClick: () -> Unit,
    onSuggestionItemSelected: (Recipe) -> Unit,
) {
    val itemHeight = 50.dp // Standard DropdownMenuItem height
    val maxVisibleItems = 3
    val dropdownHeight = itemHeight * min(suggestions.size, maxVisibleItems)
    fun shouldShowAddRecipe(): Boolean = suggestions.isEmpty() && recipeName.length >= 2
    val height by animateDpAsState(if(shouldShowAddRecipe()) itemHeight else dropdownHeight)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .verticalScroll(rememberScrollState())
            .background(
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = MaterialTheme.shapes.medium
            ),
        verticalArrangement = Arrangement.Center
    ) {
        suggestions.forEachIndexed { index, recipe ->
            DropdownMenuItem(
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RecipeItemImage(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                ),
                            imagePath = recipe.imagePath
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = recipe.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                onClick = { onSuggestionItemSelected(recipe) })
            if (index < suggestions.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
        AnimatedVisibility(shouldShowAddRecipe()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecipeItemImage(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                    imagePath = null
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = recipeName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onRecipeAddClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecipeSuggestionPreview() {
    RecipeSuggestion(
        suggestions = listOf(Utils.recipe, Utils.recipe, Utils.recipe),
        onSuggestionItemSelected = {},
        recipeName = "Recipe Name",
        onRecipeAddClick = {}
    )
}


