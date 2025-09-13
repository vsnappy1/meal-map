package com.randos.data.repository

import com.randos.data.database.dao.IngredientDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Ingredient
import com.randos.domain.repository.IngredientRepository

internal class IngredientRepositoryImpl(
    private val ingredientDao: IngredientDao
) :
    IngredientRepository {
    override suspend fun getIngredients(): List<Ingredient> {
        return ingredientDao.getAll().map { it.toDomain() }
    }

    override suspend fun getIngredient(id: Long): Ingredient? {
        return ingredientDao.get(id)?.toDomain()
    }

    override suspend fun addIngredient(ingredient: Ingredient) {
        ingredientDao.insert(ingredient.toEntity())
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.delete(ingredient.toEntity())
    }

    override suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.update(ingredient.toEntity())
    }
}
