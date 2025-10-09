package com.randos.mealmap.ui.grocery_list

import com.randos.domain.model.GroceryIngredient
import com.randos.domain.model.Ingredient
import com.randos.domain.model.RecipeIngredient
import com.randos.mealmap.utils.Utils.getWeekStartAndEnd
import com.randos.mealmap.utils.Utils.listOfWeeksAvailable
import java.time.DayOfWeek
import java.time.LocalDate

data class GroceryListScreenState(
    val isSelectingWeek: Boolean = false,
    val selectedWeek: Pair<Int, String> = listOfWeeksAvailable[1],
    val dateFrom: LocalDate = getWeekStartAndEnd(selectedWeek.first, DayOfWeek.MONDAY).first,
    val dateTo: LocalDate = getWeekStartAndEnd(selectedWeek.first, DayOfWeek.MONDAY).second,
    val groceryIngredients: List<GroceryIngredient> = listOf(
        GroceryIngredient(
            RecipeIngredient(
                Ingredient(name = "Salt"), 5.0, null
            )
        )
    )
)
