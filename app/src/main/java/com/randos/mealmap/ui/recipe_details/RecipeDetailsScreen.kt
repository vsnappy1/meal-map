package com.randos.mealmap.ui.recipe_details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RecipeDetailsScreen(
    id: Long,
    onEdit: (id: Long) -> Unit
) {
    Text(text = "Recipe Details")
}