package com.randos.domain.repository

import com.randos.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MealRepository {
    suspend fun getMealsForMealPlan(mealPlanId: Long): List<Meal>
    suspend fun getMeal(id: Long): Meal?
    suspend fun addMeal(meal: Meal)
    suspend fun deleteMeal(meal: Meal)
    suspend fun updateMeal(meal: Meal)
    suspend fun getMealsForDate(date: LocalDate): List<Meal>
    fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>>
}