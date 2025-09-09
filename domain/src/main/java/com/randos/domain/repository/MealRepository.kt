package com.randos.domain.repository

import com.randos.domain.model.Meal

interface MealRepository {
    suspend fun getMealsForMealPlan(mealPlanId: Long): List<Meal>
    suspend fun getMeal(id: Long): Meal
    suspend fun addMeal(meal: Meal, mealPlanId: Long)
    suspend fun deleteMeal(meal: Meal, mealPlanId: Long)
    suspend fun updateMeal(meal: Meal, mealPlanId: Long)
}