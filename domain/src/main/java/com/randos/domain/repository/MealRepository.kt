package com.randos.domain.repository

import com.randos.domain.model.Meal
import com.randos.domain.model.RecipeIngredient
import java.time.LocalDate

interface MealRepository {
    suspend fun getMeal(id: Long): Meal?

    suspend fun addMeal(meal: Meal): Long

    suspend fun deleteMeal(meal: Meal)

    suspend fun updateMeal(meal: Meal)

    suspend fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): List<Meal>

    suspend fun getRecipeIngredientsForDateRange(startDate: LocalDate, endDate: LocalDate): List<RecipeIngredient>
}
