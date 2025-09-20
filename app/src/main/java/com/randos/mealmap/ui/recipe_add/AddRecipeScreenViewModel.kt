package com.randos.mealmap.ui.recipe_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val ingredientsRepository: IngredientRepository
) : ViewModel() {

    private var _state = MutableLiveData(AddRecipeScreenState())
    val state: LiveData<AddRecipeScreenState> = _state

    fun onSave(onSaved: () -> Unit) {
        viewModelScope.launch {
            val recipe = getRecipe()
            recipesRepository.addRecipe(recipe.copy(dateCreated = Date()))
            onSaved()
        }
    }

    fun onIngredientTextChange(text: String) {
        if (text.length > RECIPE_INGREDIENTS_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(currentIngredientText = text))
    }

    fun onIngredientAdd(text: String) {
        viewModelScope.launch {
            val ingredient = Ingredient(name = text)
            val addedIngredient = ingredientsRepository.addIngredient(ingredient)
            val recipeIngredient =
                RecipeIngredient(ingredient = addedIngredient, quantity = 1.0, unit = null)
            onIngredientsChange(getRecipe().ingredients + recipeIngredient)
        }
        viewModelScope.launch {
            delay(50)
            onIngredientTextChange("")
        }
    }

    fun onUpdateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientsRepository.updateIngredient(ingredient)
            val ingredients = getRecipe().ingredients.map { recipeIngredient ->
                if (recipeIngredient.ingredient.id == ingredient.id) recipeIngredient.copy(
                    ingredient = ingredient
                )
                else recipeIngredient
            }
            onIngredientsChange(ingredients)
        }
    }

    fun onDeleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            val ingredients =
                getRecipe().ingredients.filter { recipeIngredient -> recipeIngredient.ingredient.id != ingredient.id }
            onIngredientsChange(ingredients)
        }
    }

    fun onInstructionTextChange(value: String) {
        if (value.length > RECIPE_INSTRUCTIONS_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(currentInstructionText = value))
    }

    fun onInstructionAdd(value: String) {
        val instructions = getRecipe().instructions + value
        onInstructionsChange(instructions)
        viewModelScope.launch {
            delay(50)
            onInstructionTextChange("")
        }
    }

    fun onUpdateInstruction(index: Int, value: String) {
        val instructions = getRecipe().instructions.toMutableList()
        instructions[index] = value
        onInstructionsChange(instructions)
    }

    fun onDeleteInstruction(index: Int) {
        val instructions = getRecipe().instructions.toMutableList()
        instructions.removeAt(index)
        onInstructionsChange(instructions)
    }

    fun onIngredientUpdateQuantity(id: Long, quantity: Double) {
        val ingredients = getRecipe().ingredients.map { recipeIngredient ->
            if (recipeIngredient.ingredient.id == id) recipeIngredient.copy(quantity = quantity)
            else recipeIngredient
        }
        onIngredientsChange(ingredients)
    }

    fun onIngredientUpdateUnit(id: Long, unit: IngredientUnit?) {
        val ingredients = getRecipe().ingredients.map { recipeIngredient ->
            if (recipeIngredient.ingredient.id == id) recipeIngredient.copy(unit = unit)
            else recipeIngredient
        }
        onIngredientsChange(ingredients)
    }

    fun onTitleChange(title: String) {
        if (title.length > RECIPE_TITLE_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(title = title)))
    }

    fun onDescriptionChange(description: String) {
        if (description.length > RECIPE_DESCRIPTION_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(description = description)))
    }

    fun onImagePathChange(imagePath: String) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(imagePath = imagePath)))
    }

    fun onInstructionsChange(instructions: List<String>) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(instructions = instructions)))
    }

    fun onIngredientsChange(ingredients: List<RecipeIngredient>) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(ingredients = ingredients)))
    }

    fun onPrepTimeChange(prepTime: String) {
        if (prepTime.length > RECIPE_PREPARATION_TIME_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(prepTime = prepTime.toIntOrNull())))
    }

    fun onCookTimeChange(cookTime: String) {
        if (cookTime.length > RECIPE_COOKING_TIME_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(cookTime = cookTime.toIntOrNull())))
    }

    fun onServingsChange(servings: Int) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(servings = servings)))
    }

    fun onTagChange(tag: RecipeTag) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(tag = tag)))
    }

    fun onCaloriesChange(calories: String) {
        if (calories.length > RECIPE_TOTAL_CALORIES_MAX_LENGTH) return
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(calories = calories.toIntOrNull())))
    }

    fun onHeavinessChange(heaviness: RecipeHeaviness) {
        _state.postValue(_state.value?.copy(recipe = getRecipe().copy(heaviness = heaviness)))
    }

    private fun getRecipe(): Recipe {
        return _state.value!!.recipe
    }
}