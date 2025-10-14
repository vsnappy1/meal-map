package com.randos.mealmap.ui.grocery

import com.randos.domain.model.GroceryIngredient
import com.randos.mealmap.utils.CalendarUtils.getWeekStartAndEnd
import com.randos.mealmap.utils.Constants.listOfWeeksAvailable
import java.time.DayOfWeek
import java.time.LocalDate

data class GroceryListScreenState(
    val isSelectingWeek: Boolean = false,
    val selectedWeek: Pair<Int, String> = listOfWeeksAvailable[1],
    val dateFrom: LocalDate = getWeekStartAndEnd(selectedWeek.first, DayOfWeek.MONDAY).first,
    val dateTo: LocalDate = getWeekStartAndEnd(selectedWeek.first, DayOfWeek.MONDAY).second,
    val groceryIngredients: List<GroceryIngredient> = emptyList()
)
