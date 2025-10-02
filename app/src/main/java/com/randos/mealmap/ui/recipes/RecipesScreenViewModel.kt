package com.randos.mealmap.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.model.Recipe
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesSort
import com.randos.domain.type.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class RecipesScreenViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableLiveData(RecipesScreenState())
    val state: LiveData<RecipesScreenState> = _state
    private var recipes: List<Recipe> = emptyList()

    fun getRecipes() {
        viewModelScope.launch {
            // TODO remove this when releasing app to production, this is only for demo purposes
            if(recipeRepository.isEmpty()){
                recipeRepository.populateSampleRecipes()
            }
            recipes = recipeRepository.getRecipes()
            _state.postValue( getState().copy(recipes = recipes))
        }
    }

    fun onSearchTextChange(text: String) {
        _state.postValue( getState().copy(searchText = text))
        applyFiltersAndSort()
    }

    fun onFilterChange(filter: RecipeTag?) {
        _state.postValue(getState().copy(filter = filter))
        applyFiltersAndSort()
    }

    fun onSortChange(sort: RecipesSort?) {
        _state.postValue( getState().copy(sort = sort))
        applyFiltersAndSort()
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        _state.postValue( getState().copy(sortOrder = sortOrder))
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        viewModelScope.launch {
            delay(50)
            val currentState = getState()
            var result = recipes

            if (currentState.searchText.isNotBlank()) {
                result = result.filter {
                    it.title.contains(currentState.searchText, ignoreCase = true)
                }
            }

            currentState.filter?.let { tag ->
                result = result.filter { it.tags.contains(tag) }
            }

            currentState.sort?.let { sort ->
                result = when (sort) {
                    RecipesSort.TITLE -> result.sortedBy { it.title }
                    RecipesSort.CREATED_DATE -> result.sortedBy { it.dateCreated }
                    RecipesSort.CALORIES -> result.sortedBy { it.calories }
                    RecipesSort.HEAVINESS -> result.sortedBy { it.heaviness }
                }
            }

            if (currentState.sortOrder == SortOrder.DESCENDING) {
                result = result.reversed()
            }

            _state.postValue(currentState.copy(recipes = result))
        }
    }

    private fun getState() = _state.value ?: RecipesScreenState()
}
