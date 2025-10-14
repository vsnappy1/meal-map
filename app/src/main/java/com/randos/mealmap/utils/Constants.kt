package com.randos.mealmap.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Icecream
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.Liquor
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.RamenDining
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesSort
import java.time.LocalDate

object Constants {
    const val RECIPE_TITLE_MAX_LENGTH = 60
    const val RECIPE_DESCRIPTION_MAX_LENGTH = 500
    const val RECIPE_INGREDIENTS_MAX_LENGTH = 200
    const val RECIPE_INSTRUCTIONS_MAX_LENGTH = 2000
    const val RECIPE_PREPARATION_TIME_MAX_LENGTH = 3
    const val RECIPE_COOKING_TIME_MAX_LENGTH = 3
    const val RECIPE_TOTAL_CALORIES_MAX_LENGTH = 5
    const val RECIPE_INGREDIENT_QUANTITY_MAX_LENGTH = 6
    const val RECIPE_ERROR_MESSAGE_SHOWN_DURATION = 1000L

    val servings = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

    val recipeSort: List<RecipesSort> = RecipesSort.entries.toList()

    val ingredientUnits = IngredientUnit.entries.toList()

    val recipeHeaviness = RecipeHeaviness.entries.toList()

    val recipeTags: List<RecipeTag> = RecipeTag.entries.toList()

    val foodIcons = listOf(
        Icons.Rounded.Fastfood,
        Icons.Rounded.LocalPizza,
        Icons.Rounded.RamenDining,
        Icons.Rounded.KebabDining,
        Icons.Rounded.Icecream,
        Icons.Rounded.DinnerDining,
        Icons.Rounded.Liquor
    )

    val listOfWeeksAvailable = listOf(
        Pair(-1, "Previous Week"),
        Pair(0, "This Week"),
        Pair(1, "Next Week")
    )

    val recipe = Recipe(
        id = 1,
        title = "Recipe Title",
        description = "Recipe Description",
        imagePath = null,
        ingredients = listOf(
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 1"),
                quantity = 1.0,
                unit = IngredientUnit.GRAM
            ),
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 2"),
                quantity = 2.5,
                unit = IngredientUnit.CUP
            ),
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 3"),
                quantity = 0.25,
                unit = IngredientUnit.PIECE
            )
        ),
        instructions = listOf("Instruction 1", "Instruction 2", "Instruction 3"),
        prepTime = 10,
        cookTime = 400,
        servings = 2,
        tags = listOf(RecipeTag.CHICKEN),
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = LocalDate.now()
    )

    val recipes = listOf(recipe)
}
