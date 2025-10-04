package com.randos.mealmap.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.Day
import com.randos.domain.type.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val recipeRepository: RecipeRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _state = MutableLiveData(HomeScreenState())
    val state: LiveData<HomeScreenState> = _state

    init {
        viewModelScope.launch {
            settingsManager.setFirstDayOfTheWeek(Day.MONDAY)
            val (dateFrom, dateTo) = getWeekRange(getState().selectedWeek)
            _state.postValue(
                getState().copy(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                )
            )
            observesMealsForWeek(dateFrom, dateTo)
        }
    }

    fun onIsSelectingWeekUpdate(isSelectingWeek: Boolean) {
        _state.postValue(getState().copy(isSelectingWeek = isSelectingWeek))
    }

    fun onSelectedWeekTextUpdate(week: Int, selectedWeekText: String) {
        val (dateFrom, dateTo) = getWeekRange(week)
        viewModelScope.launch {
            _state.postValue(
                getState().copy(
                    selectedWeekText = selectedWeekText,
                    isSelectingWeek = false,
                    selectedWeek = week,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                )
            )
        }
    }

    fun onCurrentMealEditingUpdate(triple: Triple<LocalDate, MealType, String>?) {
        _state.postValue(
            getState().copy(
                currentMealEditing = triple,
                recipeSuggestions = if (triple?.third.isNullOrEmpty()) emptyList() else getState().recipeSuggestions
            )
        )
        postRecipeSuggestions(triple?.third ?: "")
    }

    fun onAddMeal(recipe: Recipe, mealType: MealType, date: LocalDate) {
        viewModelScope.launch {
            val meal = getState().mealMap[date]?.find { it.type == mealType }
                ?: Meal(recipes = listOf(recipe), date = date, type = mealType)
            if (meal.recipes.contains(recipe)) return@launch
            if (meal.id == 0L) {
                mealRepository.addMeal(meal)
            } else {
                val updatedMeal = meal.copy(recipes = meal.recipes + recipe)
                mealRepository.updateMeal(updatedMeal)
            }
        }
    }

    private fun getState() = state.value ?: HomeScreenState()

    private fun getWeekRange(week: Int): Pair<LocalDate, LocalDate> {
        val firstDayOfTheWeek = settingsManager.getFirstDayOfTheWeek().toDayOfWeek()
        val week = LocalDate.now().plusWeeks(week.toLong())
        val weekStartDate = week.with(TemporalAdjusters.previous(firstDayOfTheWeek))
        val weekEndDate = weekStartDate.plusDays(6)
        return Pair(weekStartDate, weekEndDate)
    }

    private suspend fun observesMealsForWeek(
        dateFrom: LocalDate,
        dateTo: LocalDate
    ) {
        val map = mutableMapOf<LocalDate, List<Meal>>()
        mealRepository.getMealsForDateRange(dateFrom, dateTo).collect { meals ->
            meals.groupBy { it.date }
            for (i in 0 until Day.entries.size) {
                val date = dateFrom.plusDays(i.toLong())
                map[date] = meals.filter { it.date == date }
            }
            delay(50)
            _state.postValue(
                getState().copy(
                    isSelectingWeek = false,
                    recipeSuggestions = emptyList(),
                    currentMealEditing = null,
                    mealMap = map,
                )
            )
        }
    }

    private fun postRecipeSuggestions(query: String) {
        viewModelScope.launch {
            delay(50)
            if (query.isEmpty()) {
                _state.postValue(getState().copy(recipeSuggestions = emptyList()))
                return@launch
            }
            val suggestions = recipeRepository.getRecipesLike(query)
            _state.postValue(getState().copy(recipeSuggestions = suggestions))
        }
    }

    private fun Day.toDayOfWeek(): DayOfWeek {
        return when (this) {
            Day.MONDAY -> DayOfWeek.MONDAY
            Day.TUESDAY -> DayOfWeek.TUESDAY
            Day.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Day.THURSDAY -> DayOfWeek.THURSDAY
            Day.FRIDAY -> DayOfWeek.FRIDAY
            Day.SATURDAY -> DayOfWeek.SATURDAY
            Day.SUNDAY -> DayOfWeek.SUNDAY
        }
    }
}