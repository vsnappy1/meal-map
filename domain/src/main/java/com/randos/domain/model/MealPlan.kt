package com.randos.domain.model

data class MealPlan(
    val id: Long = 0,
    val week: Int,
    val meals: List<Meal>
)
