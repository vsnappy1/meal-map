package com.randos.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.randos.data.database.entity.RecipeIngredient

@Dao
internal interface RecipeIngredientDao {

    @Query("SELECT * FROM RecipeIngredient WHERE recipe_id=:recipeId")
    suspend fun getByRecipeId(recipeId: Long): List<RecipeIngredient>

    @Query("SELECT * FROM RecipeIngredient WHERE ingredient_id=:ingredientId")
    suspend fun getByIngredientId(ingredientId: Long): List<RecipeIngredient>

    @Query("SELECT EXISTS (SELECT 1 FROM RecipeIngredient WHERE ingredient_id=:ingredientId)")
    suspend fun isThisIngredientUsedInAnyRecipe(ingredientId: Long): Boolean

    @Query("SELECT * FROM RecipeIngredient")
    suspend fun getAll(): List<RecipeIngredient>

    @Insert
    suspend fun insert(ingredient: RecipeIngredient): Long

    @Insert
    suspend fun insertAll(vararg ingredient: RecipeIngredient)

    @Update
    suspend fun update(ingredient: RecipeIngredient)

    @Delete
    suspend fun delete(ingredient: RecipeIngredient)

    @Query("DELETE FROM RecipeIngredient WHERE recipe_id=:recipeId")
    suspend fun deleteByRecipeId(recipeId: Long)
}
