package com.randos.domain.model

import com.randos.domain.type.MealType
import java.util.Date

data class Meal(
    val id: Long,
    val recipes: List<Recipe>,
    val type: MealType,
    val date: Date
)
