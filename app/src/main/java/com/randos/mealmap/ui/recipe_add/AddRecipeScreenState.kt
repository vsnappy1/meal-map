package com.randos.mealmap.ui.recipe_add

import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
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
        tags = listOf(),
        calories = null,
        heaviness = null,
        dateCreated = Date()
    ),
    /**
     * The current text entered in the ingredient input field.
     */
    val currentIngredientText: String = "",
    /**
     * The text being edited for an existing ingredient.
     */
    val editIngredientText: String = "",
    /**
     * The index of the ingredient being edited. Null if no ingredient is being edited.
     */
    val editIngredientIndex: Int? = null,
    /**
     * The current text entered in the instruction input field.
     */
    val currentInstructionText: String = "",
    /**
     * The text being edited for an existing instruction.
     */
    val editInstructionText: String = "",
    /**
     * The index of the instruction being edited. Null if no instruction is being edited.
     */
    val editInstructionIndex: Int? = null,
    val ingredientSuggestions: List<Ingredient> = listOf(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
