package com.randos.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.MealPlanDao
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.meal1
import com.randos.data.utils.Utils.meal2
import com.randos.data.utils.Utils.mealPlan
import com.randos.domain.model.Meal
import com.randos.domain.model.MealPlan
import com.randos.domain.repository.MealPlanRepository
import com.randos.domain.repository.MealRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MealPlanRepositoryImplTest {

    private lateinit var database: MealMapDatabase
    private lateinit var mealPlanDao: MealPlanDao
    private lateinit var mealRepository: MealRepository
    private lateinit var mealPlanRepository: MealPlanRepository
    private lateinit var applicationContext: Context

    @Before
    fun setUp() {
        database = getMealMapDatabase()
        mealPlanDao = database.mealPlanDao()
        applicationContext = ApplicationProvider.getApplicationContext()
        val recipeRepository = RecipeRepositoryImpl(
            recipeDao = database.recipeDao(),
            ingredientDao = database.ingredientDao(),
            recipeIngredientDao = database.recipeIngredientDao(),
            applicationContext = applicationContext
        )
        mealRepository = MealRepositoryImpl(
            mealDao = database.mealDao(),
            mealRecipeCrossRefDao = database.mealRecipeCrossRefDao(),
            recipeRepository = recipeRepository
        )
        mealPlanRepository = MealPlanRepositoryImpl(
            mealPlanDao = mealPlanDao,
            mealRepository = mealRepository
        )

        runTest {
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
    fun getLastThree_should_return_last_three_meal_plans() = runTest {
        // Given
        addMealPlan(mealPlan, meal1, meal2)
        addMealPlan(mealPlan.copy(id = 2), meal1.copy(id = 3), meal2.copy(id = 4))
        addMealPlan(mealPlan.copy(id = 3), meal1.copy(id = 5), meal2.copy(id = 6))
        addMealPlan(mealPlan.copy(id = 4), meal1.copy(id = 7), meal2.copy(id = 8))

        // Then
        val result = mealPlanRepository.getLastThree()

        // Then
        assertEquals(3, result.size)
        assertEquals(mealPlan.copy(id = 4, meals = emptyList()), result[0])
        assertEquals(mealPlan.copy(id = 3, meals = emptyList()), result[1])
        assertEquals(mealPlan.copy(id = 2, meals = emptyList()), result[2])
    }

    @Test
    fun getPlan_should_return_meal_plan() = runTest {
        // Given
        addMealPlan(mealPlan, meal1, meal2)

        // Then
        val result = mealPlanRepository.getPlan(mealPlan.id)

        // Then
        assertEquals(mealPlan, result)
    }

    @Test
    fun addPlan_should_insert_new_plan() = runTest {
        // When
        addMealPlan(mealPlan, meal1, meal2)

        // Then
        val result = mealPlanDao.get(mealPlan.id)
        assertEquals(mealPlan.toEntity(), result)
    }

    @Test
    fun deletePlan_should_delete_meal_plan_and_meals() = runTest {
        // Given
        addMealPlan(mealPlan, meal1, meal2)

        // Then
        mealPlanRepository.deletePlan(mealPlan)

        // Then
        val result = mealPlanDao.get(mealPlan.id)
        val meal1 = mealRepository.getMeal(meal1.id)
        val meal2 = mealRepository.getMeal(meal2.id)
        assertNull(result)
        assertNull(meal1)
        assertNull(meal2)
    }

    private suspend fun addMealPlan(mealPlan: MealPlan, meal1: Meal, meal2: Meal) {
        mealPlanRepository.addPlan(mealPlan)
        mealRepository.addMeal(meal1, mealPlan.id)
        mealRepository.addMeal(meal2, mealPlan.id)
    }
}
