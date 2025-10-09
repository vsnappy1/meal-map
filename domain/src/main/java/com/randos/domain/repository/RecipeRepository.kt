package com.randos.domain.repository

import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun getRecipesLike(name: String): List<Recipe>
    suspend fun getRecipe(id: Long): Recipe?
    suspend fun getSimpleRecipe(id: Long): Recipe?
    suspend fun addRecipe(recipe: Recipe): Long
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun isEmpty(): Boolean
    suspend fun batchInsert(list: List<Recipe>)
    suspend fun populateSampleRecipes()

    suspend fun getIngredientsForRecipe(recipeId: Long): List<RecipeIngredient>
}