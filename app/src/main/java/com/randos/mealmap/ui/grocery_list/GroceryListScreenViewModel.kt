package com.randos.mealmap.ui.grocery_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.GroceryListManager
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.GroceryIngredient
import com.randos.domain.repository.MealRepository
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.utils.CalendarUtils.getWeekStartAndEnd
import com.randos.mealmap.utils.toDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class GroceryListScreenViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val settingsManager: SettingsManager,
    private val groceryListManager: GroceryListManager
) : ViewModel() {

    private val _state = MutableLiveData(GroceryListScreenState())
    val state: LiveData<GroceryListScreenState> = _state

    fun getGroceryIngredients() {
        viewModelScope.launch {
            val groceryIngredients = getGroceryIngredients(getState().selectedWeek.first)
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
                    groceryIngredients = getGroceryIngredients(week.first)
                )
            )
        }
    }

    fun onIngredientCheckedUpdate(index: Int, checked: Boolean) {
        viewModelScope.launch {
            val week = getState().selectedWeek.first
            val groceryIngredients = getState().groceryIngredients.toMutableList()
            val ingredient = groceryIngredients[index]

            if (checked) {
                groceryListManager.markIngredientAsChecked(ingredient, week)
            } else {
                groceryListManager.markIngredientAsUnchecked(ingredient, week)
            }
            groceryIngredients[index] = ingredient.copy(isChecked = checked)
            _state.postValue(getState().copy(groceryIngredients = groceryIngredients))
        }
    }

    private suspend fun getGroceryIngredients(weekOffset: Int): List<GroceryIngredient> {
        val (dateFrom, dateTo) = getWeekRange(weekOffset)
        val recipeIngredients = mealRepository.getRecipeIngredientsForDateRange(dateFrom, dateTo)
        val checkedRecipeIngredients =
            groceryListManager.getCheckedGroceryIngredientsNameForWeek(weekOffset)
        return recipeIngredients
            .groupBy { it.ingredient }
            .map { recipeIngredient ->
                GroceryIngredient(
                    name = recipeIngredient.key.name,
                    amountsByUnit = recipeIngredient.value.map { Pair(it.unit, it.quantity)  }
                )
            }
            .map { it.copy(amountsByUnit = mergeUnits(it.amountsByUnit)) }
            .map { it.copy(isChecked = checkedRecipeIngredients.contains(it.name)) }
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
