package com.randos.mealmap.ui.recipe_add

import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import java.util.Date

data class AddRecipeScreenState(
    val recipe: Recipe = Recipe(
        id = 0,
        title = "",
        description = null,
        imagePath = null,
        ingredients = listOf(),
        instructions = listOf(),
        prepTime = null,
        cookTime = null,
        servings = null,
        tag = null,
        calories = null,
        heaviness = null,
        dateCreated = Date()
    ),
    val currentIngredientText: String = "",
    val currentInstructionText: String = ""
)