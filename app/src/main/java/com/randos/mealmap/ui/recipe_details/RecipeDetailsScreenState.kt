package com.randos.mealmap.ui.recipe_details

import com.randos.domain.model.Recipe

data class RecipeDetailsScreenState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)