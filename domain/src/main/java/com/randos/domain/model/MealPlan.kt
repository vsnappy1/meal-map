package com.randos.domain.model

data class MealPlan(
    val id: Long,
    val week: Int,
    val meals: List<Meal>
)
