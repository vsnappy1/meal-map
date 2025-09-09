package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.Ingredient

@Dao
internal interface IngredientDao {

    @Query("SELECT * FROM Ingredient WHERE id=:id")
    suspend fun get(id: Long): Ingredient

    @Query("SELECT * FROM Ingredient WHERE name LIKE '%' || :name || '%'")
    suspend fun getByName(name: String): List<Ingredient>

    @Query("SELECT * FROM Ingredient")
    suspend fun getAll(): List<Ingredient>

    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Insert
    suspend fun insertAll(vararg ingredient: Ingredient)

    @Update
    suspend fun update(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)
}
