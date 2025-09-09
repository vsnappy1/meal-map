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

internal class MealRepositoryImpl(
    private val mealDao: MealDao,
    private val mealRecipeCrossRefDao: MealRecipeCrossRefDao,
    private val recipeRepository: RecipeRepository,
) : MealRepository {

    override suspend fun getMealsForMealPlan(mealPlanId: Long): List<Meal> {
        val meals = mutableListOf<Meal>()
        val mealEntities = mealDao.getByMealPlanId(mealPlanId)
        mealEntities.forEach { meal ->
            meals.add(getMeal(meal.id))
        }
        return meals
    }

    override suspend fun getMeal(id: Long): Meal {
        val recipeIds = mealRecipeCrossRefDao.getRecipesByMealId(id).map { it.recipeId }
        val recipes = mutableListOf<Recipe>()
        recipeIds.forEach { recipeId ->
            recipes.add(recipeRepository.getRecipe(recipeId))
        }
        return mealDao.get(id).toDomain(recipes)
    }

    override suspend fun addMeal(meal: Meal, mealPlanId: Long) {
        val mealId = mealDao.insert(meal.toEntity(mealPlanId))
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = mealId, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
    }

    override suspend fun deleteMeal(meal: Meal, mealPlanId: Long) {
        mealDao.delete(meal.toEntity(mealPlanId))
        // TODO check if this is needed
//        mealRecipeCrossRefDao.deleteByMealId(meal.id)
    }

    override suspend fun updateMeal(meal: Meal, mealPlanId: Long) {
        mealDao.update(meal.toEntity(mealPlanId))
        mealRecipeCrossRefDao.deleteByMealId(meal.id)
        val recipes = meal.recipes.map { MealRecipeCrossRef(mealId = meal.id, recipeId = it.id) }
        mealRecipeCrossRefDao.insertAll(*recipes.toTypedArray())
    }
}