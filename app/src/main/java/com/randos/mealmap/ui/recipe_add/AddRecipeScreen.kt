package com.randos.mealmap.ui.recipe_add

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AddRecipeScreen(id: Long? = null) { // If id is provided, it's an edit action
    Text(text = "Add Recipe")
}