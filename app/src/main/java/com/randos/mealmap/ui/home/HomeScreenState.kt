package com.randos.mealmap.ui.home

import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.type.MealType
import com.randos.mealmap.utils.Utils.getWeekStartAndEnd
import com.randos.mealmap.utils.Utils.listOfWeeksAvailable
import java.time.DayOfWeek
import java.time.LocalDate

data class HomeScreenState(
    val username: String? = null,
    val isSelectingWeek: Boolean = false,
    val weeksAvailable: List<Pair<Int, String>> = listOfWeeksAvailable,
    val selectedWeek: Int = weeksAvailable[1].first,
    val selectedWeekText: String = weeksAvailable[1].second,
    val dateFrom: LocalDate = getWeekStartAndEnd(selectedWeek, DayOfWeek.MONDAY).first,
    val dateTo: LocalDate = getWeekStartAndEnd(selectedWeek, DayOfWeek.MONDAY).second,
    val currentMealEditing: Triple<LocalDate, MealType, String>? = null,
    val recipeSuggestions: List<Recipe> = emptyList(),
    val mealMap: Map<LocalDate, List<Meal>> = getDefaultMealMap(dateFrom)
)

private fun getDefaultMealMap(dateFrom: LocalDate): Map<LocalDate, List<Meal>> {
    val map = mutableMapOf<LocalDate, List<Meal>>()
    repeat(7){
        val date = dateFrom.plusDays(it.toLong())
        map[date] = emptyList()
    }
    return map
}
