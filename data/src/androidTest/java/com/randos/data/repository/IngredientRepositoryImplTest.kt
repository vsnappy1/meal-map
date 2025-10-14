package com.randos.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.database.entity.RecipeIngredient
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.recipe1
import com.randos.domain.repository.IngredientRepository
import com.randos.domain.type.IngredientUnit
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class IngredientRepositoryImplTest {

    private lateinit var ingredientDao: IngredientDao
    private lateinit var recipeIngredientDao: RecipeIngredientDao
    private lateinit var mealMapDatabase: MealMapDatabase
    private lateinit var ingredientRepository: IngredientRepository
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        mealMapDatabase = getMealMapDatabase()
        ingredientDao = mealMapDatabase.ingredientDao()
        recipeIngredientDao = mealMapDatabase.recipeIngredientDao()
        ingredientRepository =
            IngredientRepositoryImpl(ingredientDao, recipeIngredientDao, dispatcher)
    }

    @After
    fun tearDown() {
        mealMapDatabase.close()
    }

    @Test
    fun getIngredients_should_return_ingredients_from_database() = runTest(dispatcher) {
        // Given
        ingredientDao.insert(ingredient1.toEntity())
        ingredientDao.insert(ingredient2.toEntity())

        // When
        val ingredientEntities = ingredientRepository.getIngredients()

        // Then
        assertEquals(2, ingredientEntities.size)
        assertEquals(ingredient1, ingredientEntities[0])
    }

    @Test
    fun getIngredient_should_return_ingredient_from_database() = runTest(dispatcher) {
        // Given
        ingredientDao.insert(ingredient1.toEntity())

        // When
        val ingredientEntity = ingredientRepository.getIngredient(ingredient1.id)

        // Then
        assertEquals(ingredient1, ingredientEntity)
    }

    @Test
    fun addIngredient_should_add_ingredient_to_database() = runTest(dispatcher) {
        // When
        ingredientRepository.addIngredient(ingredient1)

        // Then
        val ingredientEntity = ingredientRepository.getIngredient(ingredient1.id)
        assertEquals(ingredient1, ingredientEntity)
    }

    @Test
    fun deleteIngredient_should_delete_ingredient_from_database() = runTest(dispatcher) {
        // Given
        ingredientDao.insert(ingredient1.toEntity())

        // When
        ingredientRepository.deleteIngredient(ingredient1)

        // Then
        val ingredientEntities = ingredientRepository.getIngredients()
        assertEquals(0, ingredientEntities.size)
    }

    @Test
    fun updateIngredient_should_update_ingredient_in_database() = runTest(dispatcher) {
        // Given
        ingredientDao.insert(ingredient1.toEntity())

        // When
        ingredientRepository.updateIngredient(ingredient1.copy(name = "Tomato"))

        // Then
        val ingredientEntity = ingredientRepository.getIngredient(ingredient1.id)
        assertEquals(ingredient1.copy(name = "Tomato"), ingredientEntity)
    }

    @Test
    fun getIngredientsLike_when_ingredient_name_starts_with_search_text_should_return_ingredients() = runTest(dispatcher) {
        // Given
        ingredientDao.insert(ingredient1.toEntity())

        // When
        val ingredients = ingredientRepository.getIngredientsLike("Pot")

        // Then
        assertEquals(1, ingredients.size)
        assertEquals(ingredient1, ingredients[0])
    }

    @Test
    fun isThisIngredientUsedInAnyRecipe_when_ingredient_is_used_in_any_recipe_should_return_true() = runTest(dispatcher) {
        // Given
        val recipe = recipe1
        val ingredient = ingredient1
        val recipeIngredient = RecipeIngredient(
            id = 0,
            ingredientId = ingredient.id,
            recipeId = recipe.id,
            quantity = 1.0,
            unit = IngredientUnit.GRAM
        )
        ingredientDao.insert(ingredient.toEntity())
        mealMapDatabase.recipeDao().insert(recipe.toEntity())
        mealMapDatabase.recipeIngredientDao().insert(recipeIngredient)

        // When
        val isUsed = ingredientRepository.isThisIngredientUsedInAnyRecipe(ingredient)

        // Then
        assertTrue(isUsed)
    }

    @Test
    fun isThisIngredientUsedInAnyRecipe_when_ingredient_is_not_used_in_any_recipe_should_return_false() = runTest(dispatcher) {
        // Given
        val ingredient = ingredient1
        ingredientDao.insert(ingredient.toEntity())

        // When
        val isUsed = ingredientRepository.isThisIngredientUsedInAnyRecipe(ingredient)

        // Then
        assertFalse(isUsed)
    }
}
