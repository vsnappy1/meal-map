package com.randos.domain.repository

import com.randos.domain.model.MealPlan
import java.util.Date

interface MealPlanRepository {
    fun getPlanIds()
    fun getPlan(id: Long)
    fun addPlan(mealPlan: MealPlan)
    fun deletePlan(mealPlan: MealPlan)
    fun addMealToPlan(id: Long, date: Date, recipeId: Long, mealType: Long)
    fun removeMealFromPlan(id: Long, date: Date, recipeId: Long, mealType: Long)
}
