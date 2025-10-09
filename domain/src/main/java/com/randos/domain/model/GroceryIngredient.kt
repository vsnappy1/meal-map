package com.randos.domain.model

data class GroceryIngredient(
    val recipeIngredient: RecipeIngredient,
    val isChecked: Boolean = false
)
