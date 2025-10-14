package com.randos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.MealDao
import com.randos.data.database.dao.MealRecipeCrossRefDao
import com.randos.data.database.dao.RecipeDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.database.entity.Ingredient
import com.randos.data.database.entity.Meal
import com.randos.data.database.entity.MealRecipeCrossRef
import com.randos.data.database.entity.Recipe
import com.randos.data.database.entity.RecipeIngredient
import com.randos.data.database.util.LocalDateConverter
import com.randos.data.database.util.RecipeTagListConverter
import com.randos.data.database.util.StringListConverter

@Database(
    entities = [
        Ingredient::class,
        RecipeIngredient::class,
        Recipe::class,
        Meal::class,
        MealRecipeCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    StringListConverter::class,
    RecipeTagListConverter::class,
    LocalDateConverter::class
)
internal abstract class MealMapDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealDao(): MealDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
    abstract fun mealRecipeCrossRefDao(): MealRecipeCrossRefDao
}
