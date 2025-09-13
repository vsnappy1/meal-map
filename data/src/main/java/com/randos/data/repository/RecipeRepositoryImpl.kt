package com.randos.data.repository

import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.RecipeRepository
import jakarta.inject.Inject

internal class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val recipeIngredientDao: RecipeIngredientDao
) : RecipeRepository {
    override suspend fun getRecipes(): List<Recipe> {
        return recipeDao.getAll().map { it.toDomain(listOf()) }
    }

    override suspend fun getRecipe(id: Long): Recipe? {
        val recipeIngredients = recipeIngredientDao.get(id)
            .map { recipeIngredient -> Pair(ingredientDao.get(recipeIngredient.ingredientId), recipeIngredient.quantity) }
            .filter {it.first != null }
            .map { RecipeIngredient(it.first!!.toDomain(), it.second) }
        return recipeDao.get(id)?.toDomain(recipeIngredients)
    }

    override suspend fun addRecipe(recipe: Recipe) {
        val recipeId = recipeDao.insert(recipe.toEntity())
        val recipeIngredients = recipe.ingredients.map { it.toEntity(recipeId, it.ingredient.id) }
        /*
            The `*` is the spread operator.
            The `insertAll` function expects a variable number of arguments (vararg), like `insertAll(item1, item2, item3)`.
            However, `recipeIngredients` is a List, which is a single object.
            The spread operator `*` unpacks the elements of the array (`toTypedArray()`) and passes them as individual arguments to the function.
            So, `insertAll(*arrayOf(item1, item2))` becomes `insertAll(item1, item2)`.
         */
        recipeIngredientDao.insertAll(*recipeIngredients.toTypedArray())
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.delete(recipe.toEntity())
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.update(recipe.toEntity())
        recipeIngredientDao.deleteByRecipeId(recipe.id)
        val recipeIngredients = recipe.ingredients.map { it.toEntity(recipe.id, it.ingredient.id) }
        recipeIngredientDao.insertAll(*recipeIngredients.toTypedArray())
    }
}