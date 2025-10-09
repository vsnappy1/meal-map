package com.randos.mealmap.ui.grocery_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.GroceryIngredient
import com.randos.domain.repository.MealRepository
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.utils.Utils.getWeekStartAndEnd
import com.randos.mealmap.utils.toDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _state = MutableLiveData(GroceryListScreenState())
    val state: LiveData<GroceryListScreenState> = _state

    fun getGroceryIngredients() {
        val (dateFrom, dateTo) = getWeekRange(getState().selectedWeek.first)
        viewModelScope.launch {
            val groceryIngredients = getGroceryIngredients(dateFrom, dateTo)
            _state.postValue(getState().copy(groceryIngredients = groceryIngredients))
        }
    }

    fun onIsSelectingWeekUpdate(isSelectingWeek: Boolean) {
        _state.postValue(getState().copy(isSelectingWeek = isSelectingWeek))
    }

    fun onSelectedWeekTextUpdate(week: Pair<Int, String>) {
        val (dateFrom, dateTo) = getWeekRange(week.first)
        viewModelScope.launch {
            _state.postValue(
                getState().copy(
                    isSelectingWeek = false,
                    selectedWeek = week,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    groceryIngredients = getGroceryIngredients(dateFrom, dateTo)
                )
            )
        }
    }

    private suspend fun getGroceryIngredients(
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<GroceryIngredient> {
        val ingredients = mealRepository.getRecipeIngredientsForDateRange(dateFrom, dateTo)
        return ingredients
            .groupBy { it.ingredient.name }
            .map { (name, ingredients) ->
                GroceryIngredient(
                    name = name,
                    amountsByUnit = ingredients
                        .groupBy { it.unit }
                        .map { (unit, ingredients) -> unit to ingredients.sumOf { it.quantity } }
                )
            }
            .map { it.copy(amountsByUnit = mergeUnits(it.amountsByUnit)) }
            .sortedBy { it.name }
    }

    private fun getWeekRange(week: Int): Pair<LocalDate, LocalDate> {
        val firstDayOfTheWeek = settingsManager.getFirstDayOfTheWeek().toDayOfWeek()
        return getWeekStartAndEnd(week, firstDayOfTheWeek)
    }

    private fun getState() = state.value ?: GroceryListScreenState()
}

private fun mergeUnits(ingredients: List<Pair<IngredientUnit?, Double>>): List<Pair<IngredientUnit?, Double>> {
    val mergedIngredients = ingredients.toMutableList()
    val massIngredients = ingredients
        .filter { it.first == IngredientUnit.GRAM || it.first == IngredientUnit.KILOGRAM }
    val volumeIngredients = ingredients
        .filter {
            it.first == IngredientUnit.ML ||
                    it.first == IngredientUnit.LITER ||
                    it.first == IngredientUnit.CUP ||
                    it.first == IngredientUnit.TEASPOON ||
                    it.first == IngredientUnit.TABLESPOON
        }
    mergedIngredients.removeAll(massIngredients + volumeIngredients)

    var totalGrams = 0.0
    massIngredients.forEach { (unit, quantity) ->
        totalGrams += when (unit) {
            IngredientUnit.GRAM -> quantity
            IngredientUnit.KILOGRAM -> quantity * 1000
            else -> 0.0
        }
    }

    var totalMilliLiters = 0.0
    volumeIngredients.forEach { (unit, quantity) ->
        totalMilliLiters += when (unit) {
            IngredientUnit.ML -> quantity
            IngredientUnit.LITER -> quantity * 1000
            IngredientUnit.CUP -> quantity * 240
            IngredientUnit.TABLESPOON -> quantity * 15
            IngredientUnit.TEASPOON -> quantity * 5
            else -> 0.0
        }
    }

    if (totalGrams > 0 && totalGrams < 1000) {
        mergedIngredients.add(Pair(IngredientUnit.GRAM, totalGrams))
    }
    if (totalGrams > 1000) {
        mergedIngredients.add(Pair(IngredientUnit.KILOGRAM, totalGrams / 1000))
    }
    if (totalMilliLiters > 0 && totalMilliLiters < 1000) {
        mergedIngredients.add(Pair(IngredientUnit.ML, totalMilliLiters))
    }
    if (totalMilliLiters > 1000) {
        mergedIngredients.add(Pair(IngredientUnit.LITER, totalMilliLiters / 1000))
    }
    return mergedIngredients
}
