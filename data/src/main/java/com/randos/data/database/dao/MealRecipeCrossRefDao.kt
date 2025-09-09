package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.MealRecipeCrossRef

@Dao
internal interface MealRecipeCrossRefDao {

    @Query("SELECT * FROM MealRecipeCrossRef WHERE meal_id=:id")
    suspend fun getRecipesByMealId(id: Long): List<MealRecipeCrossRef>

    @Query("SELECT * FROM MealRecipeCrossRef")
    suspend fun getAll(): List<MealRecipeCrossRef>

    @Insert
    suspend fun insert(mealRecipeCrossRefDao: MealRecipeCrossRef): Long

    @Insert
    suspend fun insertAll(vararg mealRecipeCrossRefDao: MealRecipeCrossRef)

    @Update
    suspend fun update(mealRecipeCrossRefDao: MealRecipeCrossRef)

    @Delete
    suspend fun delete(mealRecipeCrossRefDao: MealRecipeCrossRef)

    @Query("DELETE FROM MealRecipeCrossRef WHERE meal_id=:id")
    suspend fun deleteByMealId(id: Long)
}
