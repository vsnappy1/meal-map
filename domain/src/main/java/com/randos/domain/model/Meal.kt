package com.randos.domain.model

import com.randos.domain.type.MealType
import java.time.LocalDate

data class Meal(val id: Long = 0, val recipes: List<Recipe>, val type: MealType, val date: LocalDate)
