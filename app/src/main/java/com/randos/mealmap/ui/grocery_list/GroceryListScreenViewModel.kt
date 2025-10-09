package com.randos.mealmap.ui.grocery_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.GroceryIngredient
import com.randos.domain.repository.MealRepository
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
            .sortedBy { it.name }
    }

    private fun getWeekRange(week: Int): Pair<LocalDate, LocalDate> {
        val firstDayOfTheWeek = settingsManager.getFirstDayOfTheWeek().toDayOfWeek()
        return getWeekStartAndEnd(week, firstDayOfTheWeek)
    }

    private fun getState() = state.value ?: GroceryListScreenState()
}