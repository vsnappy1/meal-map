package com.randos.data.repository

import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Ingredient
import com.randos.domain.repository.IngredientRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class IngredientRepositoryImpl @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val dispatcher: CoroutineDispatcher
) : IngredientRepository {
    override suspend fun getIngredients(): List<Ingredient> = withContext(dispatcher) {
        return@withContext ingredientDao.getAll().map { it.toDomain() }
    }

    override suspend fun getIngredientsLike(name: String): List<Ingredient> = withContext(dispatcher) {
        return@withContext ingredientDao.getByName(name).map { it.toDomain() }
    }

    override suspend fun getIngredient(id: Long): Ingredient? = withContext(dispatcher) {
        return@withContext ingredientDao.get(id)?.toDomain()
    }

    override suspend fun addIngredient(ingredient: Ingredient): Ingredient = withContext(dispatcher) {
        val id = ingredientDao.insert(ingredient.toEntity())
        return@withContext Ingredient(id = id, name = ingredient.name)
    }

    override suspend fun deleteIngredient(ingredient: Ingredient) = withContext(dispatcher) {
        ingredientDao.delete(ingredient.toEntity())
    }

    override suspend fun updateIngredient(ingredient: Ingredient) = withContext(dispatcher) {
        ingredientDao.update(ingredient.toEntity())
    }

    override suspend fun isThisIngredientUsedInAnyRecipe(ingredient: Ingredient): Boolean = withContext(dispatcher) {
        return@withContext recipeIngredientDao.isThisIngredientUsedInAnyRecipe(ingredient.id)
    }
}
