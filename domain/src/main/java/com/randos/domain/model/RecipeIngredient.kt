package com.randos.domain.model

data class RecipeIngredient(
    val id: Long,
    val ingredient: Ingredient,
    val quantity: Double
)
