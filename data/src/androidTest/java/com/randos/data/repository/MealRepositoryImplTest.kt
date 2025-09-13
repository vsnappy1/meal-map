package com.randos.data.repository

import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.MealDao
import com.randos.data.database.dao.MealRecipeCrossRefDao
import com.randos.data.database.entity.MealPlan
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.meal1
import com.randos.data.utils.Utils.meal2
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.MealType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MealRepositoryImplTest {

    private lateinit var database: MealMapDatabase
    private lateinit var mealDao: MealDao
    private lateinit var mealRecipeCrossRefDao: MealRecipeCrossRefDao
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var mealRepository: MealRepository


    @Before
    fun setUp() {
        database = getMealMapDatabase()
        mealDao = database.mealDao()
        mealRecipeCrossRefDao = database.mealRecipeCrossRefDao()
        recipeRepository = RecipeRepositoryImpl(
            recipeDao = database.recipeDao(),
            ingredientDao = database.ingredientDao(),
            recipeIngredientDao = database.recipeIngredientDao()
        )
        mealRepository = MealRepositoryImpl(mealDao, mealRecipeCrossRefDao, recipeRepository)
        runTest {
            database.ingredientDao().apply {
                insert(ingredient1.toEntity())
                insert(ingredient2.toEntity())
            }
            recipeRepository.addRecipe(meal1.recipes[0])
            recipeRepository.addRecipe(meal1.recipes[1])
            database.mealPlanDao().insert(MealPlan(1, 50))
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getMealsForMealPlan_should_return_meals_for_meal_plan() = runTest {
        // Given
        mealRepository.addMeal(meal1, 1)
        mealRepository.addMeal(meal2, 1)

        // When
        val meals = mealRepository.getMealsForMealPlan(1)

        // Then
        assertEquals(2, meals.size)
        assertEquals(meal1, meals[0])
        assertEquals(meal2, meals[1])
    }

    @Test
    fun getMeal_should_return_meal_with_recipes() = runTest {
        // Given
        mealRepository.addMeal(meal1, 1)

        // When
        val meal = mealRepository.getMeal(meal1.id)

        // Then
        assertEquals(meal1, meal)
    }

    @Test
    fun addMeal_should_insert_meal_and_cross_ref() = runTest {
        // When
        mealRepository.addMeal(meal1, 1)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal!!.id)
        assertEquals(meal1.toEntity(1), meal)
        assertEquals(2, recipes.size)
    }

    @Test
    fun deleteMeal_should_delete_meal_and_cross_ref() = runTest {
        // Given
        mealRepository.addMeal(meal1, 1)

        // When
        mealRepository.deleteMeal(meal1, 1)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal1.id)
        assertNull(meal)
        assertEquals(0, recipes.size)
    }

    @Test
    fun updateMeal_should_update_meal_and_cross_ref() = runTest {
        // Given
        mealRepository.addMeal(meal1, 1)
        val updatedMeal = meal1.copy(
            type = MealType.LUNCH,
            recipes = listOf(meal1.recipes[0].copy(title = "New Recipe"))
        )

        // When
        mealRepository.updateMeal(updatedMeal, 1)

        // Then
        val meal = mealDao.get(meal1.id)
        val recipes = mealRecipeCrossRefDao.getRecipesByMealId(meal1.id)
        assertEquals(updatedMeal.toEntity(1), meal)
        assertEquals(1, recipes.size)
        assertEquals(recipes[0].recipeId, updatedMeal.recipes[0].id)
    }
}
