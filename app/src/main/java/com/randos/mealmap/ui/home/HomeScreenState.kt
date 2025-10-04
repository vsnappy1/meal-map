package com.randos.mealmap.ui.home

import com.randos.domain.model.Meal
import com.randos.domain.model.MealPlan
import com.randos.domain.model.Recipe
import com.randos.domain.type.MealType
import java.time.LocalDate

data class HomeScreenState(
    val username: String? = null,
    val isSelectingWeek: Boolean = false,
    val weeksAvailable: List<Pair<Int, String>> = listOf(
        Pair(-1, "Previous Week"),
        Pair(0, "This Week"),
        Pair(1, "Next Week")
    ),
    val selectedWeek: Int = weeksAvailable[1].first,
    val selectedWeekText: String = weeksAvailable[1].second,
    val mealPlan: MealPlan = MealPlan(0, LocalDate.now(), LocalDate.now().plusDays(6), emptyList()),
    val dateFrom: LocalDate = LocalDate.now(),
    val dateTo: LocalDate = LocalDate.now().plusDays(6),
    val isEditingMeal: Boolean = false,
    val currentMealEditing: Triple<LocalDate, MealType, String>? = null,
    val recipeSuggestions: List<Recipe> = emptyList(),
    val mealMap: Map<LocalDate, List<Meal>> = mapOf(LocalDate.now() to emptyList())
)
