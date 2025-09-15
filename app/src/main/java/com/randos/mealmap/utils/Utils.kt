package com.randos.mealmap.utils

import com.randos.domain.model.Recipe
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
        RecipesFilter.FAVORITE,
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
}