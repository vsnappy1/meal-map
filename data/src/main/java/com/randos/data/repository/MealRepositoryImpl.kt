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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

internal class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealRecipeCrossRefDao: MealRecipeCrossRefDao,
    private val recipeRepository: RecipeRepository,
) : MealRepository {

    override suspend fun getMealsForMealPlan(mealPlanId: Long): List<Meal> {
        val meals = mutableListOf<Meal>()
        val mealEntities = mealDao.getByMealPlanId(mealPlanId)
        mealEntities.forEach { meal ->
            getMeal(meal.id)?.let { meals.add(it) }
        }
        return meals
    }

    override suspend fun getMeal(id: Long): Meal? {
        return mealDao.get(id)?.toDomain(getRecipesOfMeal(id))
    }

    override suspend fun addMeal(meal: Meal) {
        val mealId = mealDao.insert(meal.toEntity())
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = mealId, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
    }

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.delete(meal.toEntity())
    }

    override suspend fun updateMeal(meal: Meal) {
        mealDao.update(meal.toEntity())
        mealRecipeCrossRefDao.deleteByMealId(meal.id)
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = meal.id, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
    }

    override suspend fun getMealsForDate(date: LocalDate): List<Meal> {
        return mealDao.getByDate(date)
            .mapNotNull { mealDao.get(it.id)?.toDomain(getRecipesOfMeal(it.id)) }
    }

    override fun getMealsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Meal>> {
        return mealDao.getByDateRange(startDate, endDate)
            .map { list ->
                list.map { it.toDomain(getRecipesOfMeal(it.id)) }
            }
    }

    private suspend fun getRecipesOfMeal(mealId: Long): List<Recipe> {
        val recipeIds = mealRecipeCrossRefDao.getRecipesByMealId(mealId).map { it.recipeId }
        val recipes = mutableListOf<Recipe>()
        recipeIds.forEach { recipeId ->
            recipeRepository.getSimpleRecipe(recipeId)?.let { recipes.add(it) }
        }
        return recipes
    }
}