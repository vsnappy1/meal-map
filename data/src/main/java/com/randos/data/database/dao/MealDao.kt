package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.Meal

@Dao
internal interface MealDao {

    @Query("SELECT * FROM Meal WHERE id=:id")
    suspend fun get(id: Long): Meal

    @Query("SELECT * FROM Meal WHERE meal_plan_id=:id")
    suspend fun getByMealPlanId(id: Long): List<Meal>

    @Query("SELECT * FROM Meal")
    suspend fun getAll(): List<Meal>

    @Insert
    suspend fun insert(meal: Meal): Long

    @Insert
    suspend fun insertAll(vararg meal: Meal)

    @Update
    suspend fun update(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)
}
