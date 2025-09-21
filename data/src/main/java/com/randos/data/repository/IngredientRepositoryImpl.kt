package com.randos.data.repository

import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Ingredient
import com.randos.domain.repository.IngredientRepository
import jakarta.inject.Inject

internal class IngredientRepositoryImpl @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val recipeIngredientDao: RecipeIngredientDao
) :
    IngredientRepository {
    override suspend fun getIngredients(): List<Ingredient> {
        return ingredientDao.getAll().map { it.toDomain() }
    }

    override suspend fun getIngredientsLike(name: String): List<Ingredient> {
        return ingredientDao.getByName(name).map { it.toDomain() }
    }

    override suspend fun getIngredient(id: Long): Ingredient? {
        return ingredientDao.get(id)?.toDomain()
    }

    override suspend fun addIngredient(ingredient: Ingredient): Ingredient {
        val id = ingredientDao.insert(ingredient.toEntity())
        return Ingredient(id = id, name = ingredient.name)
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.delete(ingredient.toEntity())
    }

    override suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.update(ingredient.toEntity())
    }

    override suspend fun isThisIngredientUsedInAnyRecipe(ingredient: Ingredient): Boolean {
        return recipeIngredientDao.isThisIngredientUsedInAnyRecipe(ingredient.id)
    }
}
