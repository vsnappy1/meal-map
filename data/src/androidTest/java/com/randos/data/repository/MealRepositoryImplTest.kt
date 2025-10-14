package com.randos.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.MealDao
import com.randos.data.database.dao.MealRecipeCrossRefDao
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.meal1
import com.randos.domain.model.Meal
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.MealType
import java.time.LocalDate
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MealRepositoryImplTest {

    private lateinit var database: MealMapDatabase
    private lateinit var mealDao: MealDao
    private lateinit var mealRecipeCrossRefDao: MealRecipeCrossRefDao
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var mealRepository: MealRepository
    private lateinit var applicationContext: Context
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        database = getMealMapDatabase()
        mealDao = database.mealDao()
        mealRecipeCrossRefDao = database.mealRecipeCrossRefDao()
        applicationContext = ApplicationProvider.getApplicationContext()
        recipeRepository = RecipeRepositoryImpl(
            recipeDao = database.recipeDao(),
            ingredientDao = database.ingredientDao(),
            recipeIngredientDao = database.recipeIngredientDao(),
            applicationContext = applicationContext,
            dispatcher = dispatcher
        )
        mealRepository = MealRepositoryImpl(
            mealDao = mealDao,
            mealRecipeCrossRefDao = mealRecipeCrossRefDao,
            recipeRepository = recipeRepository,
            dispatcher = dispatcher
        )
        runTest(dispatcher) {
            database.ingredientDao().apply {
                insert(ingredient1.toEntity())
                insert(ingredient2.toEntity())
            }
            recipeRepository.addRecipe(meal1.recipes[0])
            recipeRepository.addRecipe(meal1.recipes[1])
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getMeal_should_return_meal_with_recipes() = runTest(dispatcher) {
        // Given
        mealRepository.addMeal(meal1)

        // When
        val meal = mealRepository.getMeal(meal1.id)

        // Then
        verifyMeal(meal1, meal)
    }

    private fun verifyMeal(expectedMeal: Meal, actualMeal: Meal?) {
        assertEquals(expectedMeal.id, actualMeal?.id)
        assertEquals(expectedMeal.type, actualMeal?.type)
        assertEquals(expectedMeal.date, actualMeal?.date)
        expectedMeal.recipes.forEachIndexed { index, recipe ->
            assertEquals(recipe.id, actualMeal?.recipes?.get(index)?.id)
            assertEquals(recipe.title, actualMeal?.recipes?.get(index)?.title)
            assertEquals(recipe.imagePath, actualMeal?.recipes?.get(index)?.imagePath)
        }
    }

    @Test
    fun addMeal_should_insert_meal_and_cross_ref() = runTest(dispatcher) {
        // When
        mealRepository.addMeal(meal1)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal!!.id)
        assertEquals(meal1.toEntity(), meal)
        assertEquals(2, recipes.size)
    }

    @Test
    fun deleteMeal_should_delete_meal_and_cross_ref() = runTest(dispatcher) {
        // Given
        mealRepository.addMeal(meal1)

        // When
        mealRepository.deleteMeal(meal1)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal1.id)
        assertNull(meal)
        assertEquals(0, recipes.size)
    }

    @Test
    fun updateMeal_should_update_meal_and_cross_ref() = runTest(dispatcher) {
        // Given
        mealRepository.addMeal(meal1)
        val updatedMeal = meal1.copy(
            type = MealType.LUNCH,
            recipes = listOf(meal1.recipes[0].copy(title = "New Recipe"))
        )

        // When
        mealRepository.updateMeal(updatedMeal)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal1.id)
        assertEquals(updatedMeal.toEntity(), meal)
        assertEquals(1, recipes.size)
        assertEquals(recipes[0].recipeId, updatedMeal.recipes[0].id)
    }

    @Test
    fun getMealsForDateRange_should_return_meals_for_date_range() = runTest(dispatcher) {
        // Given
        val date = LocalDate.of(2023, 1, 1)
        val meal1 = meal1.copy(date = date)
        val meal2 = meal1.copy(id = 2, date = date.plusDays(1))
        val meal3 = meal1.copy(id = 3, date = date.plusDays(2))
        val meal4 = meal1.copy(id = 4, date = date.plusWeeks(2))

        mealRepository.addMeal(meal1)
        mealRepository.addMeal(meal2)
        mealRepository.addMeal(meal3)
        mealRepository.addMeal(meal4)

        // When
        val meals = mealRepository.getMealsForDateRange(date, date.plusWeeks(1))

        // Then
        assertEquals(3, meals.size)
        assertTrue(meals.map { it.id }.containsAll(listOf(meal1.id, meal2.id, meal3.id)))
    }

    @Test
    fun getRecipeIngredientsForDateRange_should_return_recipe_ingredients_for_date_range() = runTest(dispatcher) {
        // Given
        val date = LocalDate.of(2023, 1, 1)
        val meal1 = meal1.copy(date = date)
        val meal2 = meal1.copy(id = 2, date = date.plusDays(1))
        val meal3 = meal1.copy(id = 3, date = date.plusDays(2))
        val meal4 = meal1.copy(id = 4, date = date.plusWeeks(2))

        mealRepository.addMeal(meal1)
        mealRepository.addMeal(meal2)
        mealRepository.addMeal(meal3)
        mealRepository.addMeal(meal4)

        // When
        val recipeIngredients =
            mealRepository.getRecipeIngredientsForDateRange(date, date.plusWeeks(1))

        // Then
        assertEquals(2, recipeIngredients.size)
    }
}
