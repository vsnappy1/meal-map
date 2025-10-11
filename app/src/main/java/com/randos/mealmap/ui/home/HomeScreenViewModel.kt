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
import com.randos.mealmap.utils.CalendarUtils.getWeekStartAndEnd
import com.randos.mealmap.utils.toDayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val recipeRepository: RecipeRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _state = MutableLiveData(HomeScreenState())
    val state: LiveData<HomeScreenState> = _state

    fun getWeekPlan() {
        viewModelScope.launch {
            val (dateFrom, dateTo) = getWeekRange(getState().selectedWeek.first)
            val mealMap = getTheMealMap(dateFrom, dateTo)
            _state.postValue(
                getState().copy(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    mealMap = mealMap,
                )
            )
        }
    }

    fun onIsSelectingWeekUpdate(isSelectingWeek: Boolean) {
        _state.postValue(getState().copy(isSelectingWeek = isSelectingWeek))
    }

    fun onSelectedWeekTextUpdate(week: Pair<Int, String>) {
        val (dateFrom, dateTo) = getWeekRange(week.first)
        viewModelScope.launch {
            val mealMap = getTheMealMap(dateFrom, dateTo)
            _state.postValue(
                getState().copy(
                    isSelectingWeek = false,
                    selectedWeek = week,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    mealMap = mealMap,
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
            if (meal?.recipes?.contains(recipe) == true) return@launch
            val updatedMeal = if (meal == null) {
                val meal = Meal(recipes = listOf(recipe), date = date, type = mealType)
                val id = mealRepository.addMeal(
                    Meal(
                        recipes = listOf(recipe),
                        date = date,
                        type = mealType
                    )
                )
                meal.copy(id = id)
            } else {
                val updatedMeal = meal.copy(recipes = meal.recipes + recipe)
                mealRepository.updateMeal(updatedMeal)
                updatedMeal
            }
            updateMeal(updatedMeal, mealType, date)
        }
    }

    fun onAddNewRecipe(title: String, mealType: MealType, date: LocalDate) {
        viewModelScope.launch {
            var recipe = Recipe(title = title, dateCreated = LocalDate.now())
            val recipeId = recipeRepository.addRecipe(recipe)
            recipe = recipe.copy(id = recipeId)
            onAddMeal(recipe, mealType, date)
        }
    }

    fun onRemoveMeal(recipe: Recipe, mealType: MealType, date: LocalDate) {
        viewModelScope.launch {
            val meal = getState().mealMap[date]?.find { it.type == mealType } ?: return@launch
            val recipes = meal.recipes.toMutableList()
            recipes.remove(recipe)
            val updatedMeal = meal.copy(recipes = recipes)
            mealRepository.updateMeal(updatedMeal)
            if (recipes.isEmpty()) {
                mealRepository.deleteMeal(updatedMeal)
            }
            updateMeal(updatedMeal, mealType, date)
        }
    }

    private fun updateMeal(
        meal: Meal,
        mealType: MealType,
        date: LocalDate,
    ) {
        val currentMealsForDate = getState().mealMap[date] ?: emptyList()
        val mealExistsForType = currentMealsForDate.any { it.type == mealType }

        val meals = if (mealExistsForType) {
            currentMealsForDate.map { if (it.type == mealType) meal else it }
        } else {
            currentMealsForDate + meal
        }.filter { it.recipes.isNotEmpty() }

        val newMealMap = getState().mealMap.toMutableMap()
        newMealMap[date] = meals
        _state.postValue(
            getState().copy(
                mealMap = newMealMap,
                recipeSuggestions = emptyList(),
                currentMealEditing = null
            )
        )
    }

    private fun getState() = state.value ?: HomeScreenState()

    private fun getWeekRange(week: Int): Pair<LocalDate, LocalDate> {
        val firstDayOfTheWeek = settingsManager.getFirstDayOfTheWeek().toDayOfWeek()
        return getWeekStartAndEnd(week, firstDayOfTheWeek)
    }

    private suspend fun getTheMealMap(
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Map<LocalDate, List<Meal>> {
        val map = mutableMapOf<LocalDate, List<Meal>>()
        val meals = mealRepository.getMealsForDateRange(dateFrom, dateTo).groupBy { it.date }
        for (i in 0 until Day.entries.size) {
            val date = dateFrom.plusDays(i.toLong())
            map[date] = meals[date] ?: emptyList()
        }
        return map
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
}