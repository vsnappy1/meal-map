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
import kotlinx.coroutines.launch

@HiltViewModel
class RecipesScreenViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableLiveData(RecipesScreenState())
    val state: LiveData<RecipesScreenState> = _state
    private var recipes: List<Recipe> = emptyList()
    private var filteredRecipes: List<Recipe> = emptyList()

    fun getRecipes() {
        viewModelScope.launch {
            recipes = recipeRepository.getRecipes()
            filteredRecipes = recipes
            _state.postValue(_state.value?.copy(recipes = recipes))
        }
    }

    fun onSearchTextChange(text: String) {
        filteredRecipes = recipes.filter { it.title.contains(text, ignoreCase = true) }
        _state.postValue(_state.value?.copy(searchText = text, recipes = filteredRecipes))
    }

    fun onFilterChange(filter: RecipeTag?) {
        _state.postValue(_state.value?.copy(filter = filter))
    }

    fun onSortChange(sort: RecipesSort?) {
        filteredRecipes = when (sort) {
            RecipesSort.TITLE -> filteredRecipes.sortedBy { it.title }
            RecipesSort.CREATED_DATE -> filteredRecipes.sortedBy { it.dateCreated }
            RecipesSort.CALORIES -> filteredRecipes.sortedBy { it.calories }
            RecipesSort.HEAVINESS -> filteredRecipes.sortedBy { it.heaviness }
            null -> filteredRecipes
        }
        var sortOrder = _state.value?.sortOrder ?: SortOrder.ASCENDING
        if (sort == null) {
            if (sortOrder == SortOrder.DESCENDING) {
                filteredRecipes = filteredRecipes.reversed()
                sortOrder = SortOrder.ASCENDING
            }
        }
        _state.postValue(
            _state.value?.copy(
                sort = sort,
                sortOrder = sortOrder,
                recipes = filteredRecipes
            )
        )
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        filteredRecipes = this.filteredRecipes.reversed()
        _state.postValue(_state.value?.copy(sortOrder = sortOrder, recipes = filteredRecipes))
    }
}
