package com.randos.domain.repository

import com.randos.domain.model.Recipe

interface RecipeRepository {
    fun getRecipes()
    fun getRecipe(id: Long)
    fun addRecipe(recipe: Recipe)
    fun deleteRecipe(recipe: Recipe)
    fun updateRecipe(recipe: Recipe)
}