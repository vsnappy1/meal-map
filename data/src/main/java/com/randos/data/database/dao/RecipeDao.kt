package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.Recipe

@Dao
internal interface RecipeDao {

    @Query("SELECT * FROM Recipe WHERE id=:id")
    suspend fun get(id: Long): Recipe?

    @Query("SELECT * FROM Recipe WHERE title LIKE '%' || :title || '%'")
    suspend fun getByName(title: String): List<Recipe>

    @Query("SELECT * FROM Recipe")
    suspend fun getAll(): List<Recipe>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Insert
    suspend fun insertAll(vararg recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT COUNT(*) FROM Recipe")
    fun getRecipeCount(): Int
}
