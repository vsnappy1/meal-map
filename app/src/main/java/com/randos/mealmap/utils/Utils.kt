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
import com.randos.domain.type.RecipesFilter
import com.randos.domain.type.RecipesSort
import java.util.Date

object Utils {
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
        tag = RecipeTag.CHICKEN,
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = Date()
    )
    val recipes = listOf(recipe)

    val servings = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

    val recipeFilters = RecipesFilter.entries.toList()

    val recipeSort = RecipesSort.entries.toList()

    val ingredientUnits = IngredientUnit.entries.toList()

    val recipeHeaviness = RecipeHeaviness.entries.toList()

    val recipeTags = RecipeTag.entries.toList()

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