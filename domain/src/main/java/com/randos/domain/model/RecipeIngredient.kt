package com.randos.domain.model

import com.randos.domain.type.IngredientUnit

data class RecipeIngredient(val ingredient: Ingredient, val quantity: Double, val unit: IngredientUnit?)
