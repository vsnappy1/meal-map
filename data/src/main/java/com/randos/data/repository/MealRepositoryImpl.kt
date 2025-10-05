package com.randos.data.repository

import com.randos.data.database.dao.MealDao
import com.randos.data.database.dao.MealRecipeCrossRefDao
import com.randos.data.database.entity.MealRecipeCrossRef
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate

internal class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealRecipeCrossRefDao: MealRecipeCrossRefDao,
    private val recipeRepository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher
) : MealRepository {

    override suspend fun getMealsForMealPlan(mealPlanId: Long): List<Meal> =
        withContext(dispatcher) {
            val meals = mutableListOf<Meal>()
            val mealEntities = mealDao.getByMealPlanId(mealPlanId)
            mealEntities.forEach { meal ->
                getMeal(meal.id)?.let { meals.add(it) }
            }
            return@withContext meals
        }

    override suspend fun getMeal(id: Long): Meal? = withContext(dispatcher) {
        return@withContext mealDao.get(id)?.toDomain(getRecipesOfMeal(id))
    }

    override suspend fun addMeal(meal: Meal): Long = withContext(dispatcher) {
        val mealId = mealDao.insert(meal.toEntity())
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = mealId, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
        return@withContext mealId
    }

    override suspend fun deleteMeal(meal: Meal) = withContext(dispatcher) {
        mealDao.delete(meal.toEntity())
    }

    override suspend fun updateMeal(meal: Meal) = withContext(dispatcher) {
        mealDao.update(meal.toEntity())
        mealRecipeCrossRefDao.deleteByMealId(meal.id)
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = meal.id, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
    }

    override suspend fun getMealsForDate(date: LocalDate): List<Meal> = withContext(dispatcher) {
        return@withContext mealDao.getByDate(date)
            .mapNotNull { mealDao.get(it.id)?.toDomain(getRecipesOfMeal(it.id)) }
    }

    override suspend fun getMealsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Meal> = withContext(dispatcher) {
        return@withContext mealDao.getByDateRange(startDate, endDate).map {
            it.toDomain(getRecipesOfMeal(it.id))
        }
    }

    private suspend fun getRecipesOfMeal(mealId: Long): List<Recipe> = withContext(dispatcher) {
        val recipeIds = mealRecipeCrossRefDao.getRecipesByMealId(mealId).map { it.recipeId }
        val recipes = mutableListOf<Recipe>()
        recipeIds.forEach { recipeId ->
            recipeRepository.getSimpleRecipe(recipeId)?.let { recipes.add(it) }
        }
        return@withContext recipes
    }
}