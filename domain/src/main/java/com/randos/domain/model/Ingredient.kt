package com.randos.domain.model

import com.randos.domain.type.IngredientUnit

data class Ingredient (
    val id: Long,
    val name: String,
    val unit: IngredientUnit,
    val calories: Int
)
