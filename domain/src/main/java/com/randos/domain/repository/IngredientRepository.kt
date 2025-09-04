package com.randos.domain.repository

import com.randos.domain.model.Ingredient

interface IngredientRepository {
    fun getIngredients()
    fun getIngredient(id: Long)
    fun addIngredient(ingredient: Ingredient)
    fun deleteIngredient(ingredient: Ingredient)
    fun updateIngredient(ingredient: Ingredient)
}