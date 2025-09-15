package com.randos.mealmap.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.RecipesFilter
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

    fun getRecipes() {
        viewModelScope.launch {
            val recipes = recipeRepository.getRecipes()
            _state.postValue(_state.value?.copy(recipes = recipes))
        }
    }

    fun onSearchTextChange(text: String) {
        _state.postValue(_state.value?.copy(searchText = text))
    }

    fun onFilterChange(filter: RecipesFilter?) {
        _state.postValue(_state.value?.copy(filter = filter))
    }

    fun onSortChange(sort: RecipesSort?) {
        _state.postValue(_state.value?.copy(sort = sort))
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        _state.postValue(_state.value?.copy(sortOrder = sortOrder))
    }
}
