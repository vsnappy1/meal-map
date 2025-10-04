package com.randos.domain.model

import java.time.LocalDate

data class MealPlan(
    val id: Long = 0,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val meals: List<Meal>
)
