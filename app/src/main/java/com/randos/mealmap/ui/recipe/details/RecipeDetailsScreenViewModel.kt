package com.randos.mealmap.ui.recipe.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.model.Recipe
import com.randos.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RecipeDetailsScreenViewModel @Inject constructor(private val recipeRepository: RecipeRepository) : ViewModel() {

    private val _state = MutableLiveData(RecipeDetailsScreenState())
    val state: LiveData<RecipeDetailsScreenState> = _state

    fun getRecipeDetails(id: Long) {
        viewModelScope.launch {
            val recipe = recipeRepository.getRecipe(id)
            _state.value = _state.value?.copy(recipe = recipe)
        }
    }

    fun deleteRecipe(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val recipe = getRecipe() ?: return@launch
            recipeRepository.deleteRecipe(recipe)
            onDeleted()
        }
    }

    private fun getRecipe(): Recipe? = _state.value?.recipe
}
