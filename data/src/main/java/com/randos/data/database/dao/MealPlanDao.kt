package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.MealPlan

@Dao
internal interface MealPlanDao {

    @Query("SELECT * FROM MealPlan WHERE id=:id")
    suspend fun get(id: Long): MealPlan

    @Query("SELECT * FROM MealPlan")
    suspend fun getAll(): List<MealPlan>

    /**
     * Retrieves the last three MealPlan entries, ordered by their ID in descending order
     * (newest first).
     */
    @Query("SELECT * FROM MealPlan ORDER BY id DESC LIMIT 3")
    suspend fun getLastThree(): List<MealPlan>

    @Insert
    suspend fun insert(mealPlan: MealPlan): Long

    @Insert
    suspend fun insertAll(vararg mealPlan: MealPlan)

    @Update
    suspend fun update(mealPlan: MealPlan)

    @Delete
    suspend fun delete(mealPlan: MealPlan)
}
