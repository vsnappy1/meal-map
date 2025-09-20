package com.randos.mealmap.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Icecream
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.Liquor
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.RamenDining
import com.randos.domain.model.Recipe
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesFilter
import com.randos.domain.type.RecipesSort
import java.util.Date

object Utils {
    val recipe = Recipe(
        id = 1,
        title = "Recipe Title",
        description = "Recipe Description",
        imagePath = null,
        ingredients = listOf(),
        instructions = listOf(),
        prepTime = 10,
        cookTime = 20,
        servings = 2,
        tag = RecipeTag.CHICKEN,
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = Date()
    )
    val recipes = listOf(recipe)

    val recipeFilters = listOf(
        RecipesFilter.BREAKFAST,
        RecipesFilter.LUNCH,
        RecipesFilter.DINNER,
        RecipesFilter.SNACK
    )

    val recipeSort = listOf(
        RecipesSort.TITLE,
        RecipesSort.CALORIES,
        RecipesSort.HEAVINESS,
        RecipesSort.CREATED_DATE
    )

    val ingredientUnits = listOf(
        IngredientUnit.GRAM,
        IngredientUnit.CUP,
        IngredientUnit.TEASPOON,
        IngredientUnit.TABLESPOON,
        IngredientUnit.KILOGRAM,
        IngredientUnit.ML,
        IngredientUnit.LITER,
        IngredientUnit.PIECE
    )

    val servings = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

    val recipeHeaviness = listOf(
        RecipeHeaviness.LIGHT,
        RecipeHeaviness.MEDIUM,
        RecipeHeaviness.HEAVY
    )

    val recipeTags = listOf(
        RecipeTag.QUICK,
        RecipeTag.SEA_FOOD,
        RecipeTag.CHICKEN,
        RecipeTag.LENTIL,
        RecipeTag.VEGITABLE
    )

    val foodIcons = listOf(
        Icons.Rounded.Fastfood,
        Icons.Rounded.LocalPizza,
        Icons.Rounded.RamenDining,
        Icons.Rounded.KebabDining,
        Icons.Rounded.Icecream,
        Icons.Rounded.DinnerDining,
        Icons.Rounded.Liquor
    )

}