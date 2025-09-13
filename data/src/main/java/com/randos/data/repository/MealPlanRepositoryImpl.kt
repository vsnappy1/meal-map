package com.randos.data.repository

import com.randos.data.database.dao.MealPlanDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.MealPlan
import com.randos.domain.repository.MealPlanRepository
import com.randos.domain.repository.MealRepository

internal class MealPlanRepositoryImpl(
    private val mealPlanDao: MealPlanDao,
    private val mealRepository: MealRepository,
) : MealPlanRepository {

    override suspend fun getLastThree(): List<MealPlan> {
        return mealPlanDao.getLastThree().map { it.toDomain(emptyList()) }
    }

    override suspend fun getPlan(id: Long): MealPlan? {
        val meals = mealRepository.getMealsForMealPlan(id)
        return mealPlanDao.get(id)?.toDomain(meals)
    }

    override suspend fun addPlan(mealPlan: MealPlan) {
       mealPlanDao.insert(mealPlan.toEntity())
    }

    override suspend fun deletePlan(mealPlan: MealPlan) {
        mealPlanDao.delete(mealPlan.toEntity())
    }
}