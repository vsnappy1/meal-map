package com.randos.domain.repository

import com.randos.domain.model.Ingredient

interface IngredientRepository {
    suspend fun getIngredients(): List<Ingredient>
    suspend fun getIngredientsLike(name: String): List<Ingredient>
    suspend fun getIngredient(id: Long): Ingredient?
    suspend fun addIngredient(ingredient: Ingredient): Ingredient
    suspend fun deleteIngredient(ingredient: Ingredient)
    suspend fun updateIngredient(ingredient: Ingredient)

    suspend fun isThisIngredientUsedInAnyRecipe(ingredient: Ingredient): Boolean
}