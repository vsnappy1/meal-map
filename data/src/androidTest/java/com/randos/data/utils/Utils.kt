package com.randos.data.utils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.randos.data.database.MealMapDatabase
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Meal
import com.randos.domain.model.MealPlan
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.MealType
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import java.util.Date

internal object Utils {
    fun getMealMapDatabase(): MealMapDatabase {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return Room.inMemoryDatabaseBuilder(
            context, MealMapDatabase::class.java
        ).build()
    }

    val ingredient1 = Ingredient(
        id = 10,
        name = "Potato",
    )

    val ingredient2 = Ingredient(
        id = 11,
        name = "Tomato",
    )

    val recipe1 = Recipe(
        id = 1,
        title = "Recipe 1",
        description = "Recipe 1 description",
        imagePath = "",
        instructions = listOf("Step 1", "Step 2"),
        ingredients = listOf(
            RecipeIngredient(ingredient1, 2.0, IngredientUnit.GRAM),
            RecipeIngredient(ingredient2, 3.0, IngredientUnit.GRAM)
        ),
        prepTime = 10,
        cookTime = 20,
        servings = 2,
        tag = RecipeTag.CHICKEN,
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = Date()
    )

    val recipe2 = Recipe(
        id = 2,
        title = "Recipe 2",
        description = "Recipe 2 description",
        imagePath = "",
        instructions = listOf("Step 1", "Step 2"),
        ingredients = listOf(
            RecipeIngredient(ingredient1, 5.0, IngredientUnit.GRAM),
            RecipeIngredient(ingredient2, 1.0, IngredientUnit.GRAM)
        ),
        prepTime = 10,
        cookTime = 20,
        servings = 4,
        tag = RecipeTag.CHICKEN,
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = Date()
    )

    val meal1 = Meal(
        id = 1,
        recipes = listOf(recipe1, recipe2),
        type = MealType.BREAKFAST,
        date = Date()
    )

    val meal2 = Meal(
        id = 2,
        recipes = listOf(recipe1, recipe2),
        type = MealType.LUNCH,
        date = Date()
    )

    val mealPlan = MealPlan(
        id = 1,
        meals = listOf(meal1, meal2),
        week = 50
    )
}