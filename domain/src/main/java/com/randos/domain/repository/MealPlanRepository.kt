package com.randos.domain.repository

import com.randos.domain.model.MealPlan

interface MealPlanRepository {
    suspend fun getLastThree(): List<MealPlan>
    suspend fun getPlan(id: Long): MealPlan?
    suspend fun addPlan(mealPlan: MealPlan)
    suspend fun deletePlan(mealPlan: MealPlan)
}
