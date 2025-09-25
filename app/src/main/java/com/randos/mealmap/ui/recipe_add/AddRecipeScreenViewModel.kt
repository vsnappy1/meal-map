package com.randos.mealmap.ui.recipe_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.IngredientRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.mealmap.utils.Constants.RECIPE_COOKING_TIME_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_DESCRIPTION_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_ERROR_MESSAGE_SHOWN_DURATION
import com.randos.mealmap.utils.Constants.RECIPE_INGREDIENTS_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_INSTRUCTIONS_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_PREPARATION_TIME_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_TITLE_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_TOTAL_CALORIES_MAX_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

@HiltViewModel
class AddRecipeScreenViewModel @Inject constructor(
    private val recipesRepository: RecipeRepository,
    private val ingredientsRepository: IngredientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var recipeId: Long? = savedStateHandle["id"]
    private var _state = MutableLiveData(AddRecipeScreenState(isLoading = recipeId != null))
    val state: LiveData<AddRecipeScreenState> = _state

    init {
        viewModelScope.launch {
            val id = recipeId
            if (id == null) return@launch
            val recipe = recipesRepository.getRecipe(id) ?: return@launch
            _state.postValue(getState().copy(recipe = recipe, isLoading = false))
        }
    }

    fun onSave(imagePath: String? = null, onSaved: () -> Unit) {
        viewModelScope.launch {
            var recipe = getRecipe()
            if (imagePath != null) {
                recipe = recipe.copy(imagePath = imagePath)
            }
            if (recipeId != null) recipesRepository.updateRecipe(recipe)
            else recipesRepository.addRecipe(recipe.copy(dateCreated = Date()))
            onSaved()
        }
    }

    fun onIngredientTextChange(text: String) {
        if (text.length > RECIPE_INGREDIENTS_MAX_LENGTH) return
        _state.postValue(getState().copy(currentIngredientText = text))
        findSuggestion(text)
    }

    fun onIngredientEditTextChange(value: String) {
        if (value.length > RECIPE_INGREDIENTS_MAX_LENGTH) return
        _state.postValue(getState().copy(editIngredientText = value))
        findSuggestion(value)
    }

    fun onIngredientIsEditingChange(index: Int, value: Boolean) {
        val newIndex = if (value) index else null
        val ingredient = if (value) getRecipe().ingredients[index].ingredient.name else ""
        viewModelScope.launch {
            if (!value) delay(50)
            _state.postValue(
                getState().copy(
                    editIngredientIndex = newIndex,
                    editIngredientText = ingredient
                )
            )
        }
    }

    fun onIngredientAdd(text: String) {
        viewModelScope.launch {
            val ingredient = Ingredient(name = text.trim())
            if (doesIngredientAlreadyExistInIngredients(ingredient)) return@launch
            val addedIngredient = ingredientsRepository.addIngredient(ingredient)
            val recipeIngredient =
                RecipeIngredient(ingredient = addedIngredient, quantity = 1.0, unit = null)
            val ingredients = getRecipe().ingredients + recipeIngredient
            _state.postValue(
                getState().copy(
                    recipe = getRecipe().copy(ingredients = ingredients),
                    currentIngredientText = "",
                    ingredientSuggestions = listOf(),
                    editIngredientText = "",
                    editIngredientIndex = null
                )
            )
        }
    }

    fun onIngredientUpdate(index: Int, text: String) {
        viewModelScope.launch {
            val ingredient = Ingredient(name = text.trim())
            if (doesIngredientAlreadyExistInIngredients(ingredient)) return@launch
            val addedIngredient = ingredientsRepository.addIngredient(ingredient)
            val ingredients = getRecipe().ingredients.toMutableList()
            ingredients[index] = ingredients[index].copy(ingredient = addedIngredient)
            onIngredientsChange(ingredients)
        }
    }

    fun onIngredientDelete(index: Int) {
        val ingredients = getRecipe().ingredients.toMutableList()
        ingredients.removeAt(index)
        onIngredientsChange(ingredients)
    }

    fun onIngredientUpdateQuantity(index: Int, quantity: Double) {
        val ingredients = getRecipe().ingredients.toMutableList()
        ingredients[index] = ingredients[index].copy(quantity = quantity)
        onIngredientsChange(ingredients)
    }

    fun onIngredientUpdateUnit(index: Int, unit: IngredientUnit?) {
        val ingredients = getRecipe().ingredients.toMutableList()
        ingredients[index] = ingredients[index].copy(unit = unit)
        onIngredientsChange(ingredients)
    }

    fun onInstructionTextChange(value: String) {
        if (value.length > RECIPE_INSTRUCTIONS_MAX_LENGTH) return
        _state.postValue(getState().copy(currentInstructionText = value))
    }

    fun onInstructionEditTextChange(value: String) {
        if (value.length > RECIPE_INSTRUCTIONS_MAX_LENGTH) return
        _state.postValue(getState().copy(editInstructionText = value))
    }

    fun onInstructionIsEditingChange(index: Int, value: Boolean) {
        val newIndex = if (value) index else null
        val instruction = if (value) getRecipe().instructions[index] else ""
        viewModelScope.launch {
            if (!value) delay(50)
            _state.postValue(
                getState().copy(
                    editInstructionIndex = newIndex,
                    editInstructionText = instruction
                )
            )
        }
    }

    fun onInstructionAdd(value: String) {
        val instructions = getRecipe().instructions + value
        _state.postValue(
            getState().copy(
                recipe = getRecipe().copy(instructions = instructions),
                currentInstructionText = ""
            )
        )
    }

    fun onInstructionUpdate(index: Int, value: String) {
        val instructions = getRecipe().instructions.toMutableList()
        instructions[index] = value
        onInstructionsChange(instructions)
    }

    fun onInstructionDelete(index: Int) {
        val instructions = getRecipe().instructions.toMutableList()
        instructions.removeAt(index)
        onInstructionsChange(instructions)
    }

    fun onImagePathChange(imagePath: String) {
        _state.postValue(getState().copy(recipe = getRecipe().copy(imagePath = imagePath)))
    }

    fun onTitleChange(title: String) {
        if (title.length > RECIPE_TITLE_MAX_LENGTH) return
        _state.postValue(getState().copy(recipe = getRecipe().copy(title = title)))
    }

    fun onDescriptionChange(description: String) {
        if (description.length > RECIPE_DESCRIPTION_MAX_LENGTH) return
        _state.postValue(getState().copy(recipe = getRecipe().copy(description = description)))
    }

    fun onIngredientsChange(ingredients: List<RecipeIngredient>) {
        _state.postValue(getState().copy(recipe = getRecipe().copy(ingredients = ingredients)))
    }

    fun onInstructionsChange(instructions: List<String>) {
        _state.postValue(getState().copy(recipe = getRecipe().copy(instructions = instructions)))
    }

    fun onPrepTimeChange(prepTime: String) {
        if (prepTime.length > RECIPE_PREPARATION_TIME_MAX_LENGTH) return
        _state.postValue(getState().copy(recipe = getRecipe().copy(prepTime = prepTime.toIntOrNull())))
    }

    fun onCookTimeChange(cookTime: String) {
        if (cookTime.length > RECIPE_COOKING_TIME_MAX_LENGTH) return
        _state.postValue(getState().copy(recipe = getRecipe().copy(cookTime = cookTime.toIntOrNull())))
    }

    fun onServingsChange(servings: Int) {
        _state.postValue(getState().copy(recipe = getRecipe().copy(servings = servings)))
    }

    fun onTagClick(tag: RecipeTag) {
        val tags = getRecipe().tags.toMutableList()
        if (tags.contains(tag)) tags.remove(tag)
        else tags.add(tag)
        _state.postValue(getState().copy(recipe = getRecipe().copy(tags = tags)))
    }

    fun onHeavinessChange(heaviness: RecipeHeaviness) {
        _state.postValue(getState().copy(recipe = getRecipe().copy(heaviness = heaviness)))
    }

    fun onCaloriesChange(calories: String) {
        if (calories.length > RECIPE_TOTAL_CALORIES_MAX_LENGTH) return
        _state.postValue(getState().copy(recipe = getRecipe().copy(calories = calories.toIntOrNull())))
    }

    fun onSuggestionItemSelected(index: Int, ingredient: Ingredient) {
        viewModelScope.launch {
            if (doesIngredientAlreadyExistInIngredients(ingredient)) return@launch
            val ingredients = getRecipe().ingredients.toMutableList()
            if (index == ingredients.size) {
                val ing = RecipeIngredient(ingredient = ingredient, quantity = 1.0, unit = null)
                ingredients.add(ing)
            } else {
                ingredients[index] = ingredients[index].copy(ingredient = ingredient)
            }
            _state.postValue(
                getState().copy(
                    recipe = getRecipe().copy(ingredients = ingredients),
                    ingredientSuggestions = listOf(),
                    currentIngredientText = "",
                    editIngredientText = "",
                    editIngredientIndex = null
                )
            )
        }
    }

    fun onDeleteSuggestedIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            if (ingredientsRepository.isThisIngredientUsedInAnyRecipe(ingredient) ||
                isIngredientAlreadyExistInIngredients(ingredient)
            ) {
                _state.postValue(
                    getState().copy(
                        errorMessage = "Cannot delete: This ingredient is part of one or more recipes."
                    )
                )
                delay(RECIPE_ERROR_MESSAGE_SHOWN_DURATION)
                _state.postValue(
                    getState().copy(
                        errorMessage = null
                    )
                )
                return@launch
            }
            ingredientsRepository.deleteIngredient(ingredient)
            val suggestions = getState().ingredientSuggestions.toMutableList()
            suggestions.remove(ingredient)
            _state.postValue(getState().copy(ingredientSuggestions = suggestions))
        }
    }

    private fun getRecipe(): Recipe {
        return _state.value!!.recipe
    }

    private fun getState(): AddRecipeScreenState {
        return _state.value!!
    }

    private suspend fun doesIngredientAlreadyExistInIngredients(ingredient: Ingredient): Boolean {
        if (isIngredientAlreadyExistInIngredients(ingredient)) {
            _state.postValue(
                getState().copy(
                    currentIngredientText = "",
                    errorMessage = "Duplicate ingredient: This ingredient is already part of the current recipe."
                )
            )
            delay(RECIPE_ERROR_MESSAGE_SHOWN_DURATION)
            _state.postValue(
                getState().copy(
                    errorMessage = null
                )
            )
            return true
        }
        return false
    }

    private fun isIngredientAlreadyExistInIngredients(ingredient: Ingredient): Boolean =
        getRecipe().ingredients.find { it.ingredient.name == ingredient.name } != null

    private fun findSuggestion(text: String) {
        viewModelScope.launch {
            delay(50)
            if (text.isBlank()) {
                _state.postValue(getState().copy(ingredientSuggestions = listOf()))
                return@launch
            }
            val suggestions = ingredientsRepository.getIngredientsLike(text)
            _state.postValue(getState().copy(ingredientSuggestions = suggestions))
        }
    }
}
