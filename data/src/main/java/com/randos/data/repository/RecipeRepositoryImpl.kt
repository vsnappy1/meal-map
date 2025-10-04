package com.randos.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import com.randos.data.R
import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toDomain
import com.randos.data.mapper.toEntity
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.RecipeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.time.LocalDate

internal class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val applicationContext: Context
) : RecipeRepository {
    override suspend fun getRecipes(): List<Recipe> {
        return recipeDao.getAll().map { it.toDomain(listOf()) }
    }

    override suspend fun getRecipesLike(name: String): List<Recipe> {
        return recipeDao.getByName(name).map { it.toDomain(listOf()) }
    }

    override suspend fun getRecipe(id: Long): Recipe? {
        val recipeIngredients = recipeIngredientDao.getByRecipeId(id)
            .map { recipeIngredient ->
                Triple(
                    ingredientDao.get(recipeIngredient.ingredientId),
                    recipeIngredient.quantity,
                    recipeIngredient.unit
                )
            }
            .filter { it.first != null }
            .map {
                RecipeIngredient(
                    ingredient = it.first!!.toDomain(),
                    quantity = it.second,
                    unit = it.third
                )
            }
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

    override suspend fun isEmpty(): Boolean = withContext(Dispatchers.IO) {
        return@withContext recipeDao.getRecipeCount() == 0
    }

    override suspend fun batchInsert(list: List<Recipe>) = withContext(Dispatchers.IO) {
        // 1. Prepare and insert all unique ingredients from all recipes
        //    We set id to 0 for new ingredients to let Room auto-generate IDs.
        //    If an ingredient might already exist based on its name, you'll need
        //    a more sophisticated "upsert" (insert or update) logic for ingredients.
        //    For simplicity, this example assumes new ingredients or you handle duplicates elsewhere.
        val allIngredientsFromRecipes =
            list.flatMap { it.ingredients.map { recipeIngredient -> recipeIngredient.ingredient } }
        val uniqueIngredientEntities = allIngredientsFromRecipes
            .distinctBy { it.name } // Assuming 'name' is a unique identifier for an ingredient
            .map { it.copy(id = 0).toEntity() } // Ensure ID is 0 for auto-generation

        val insertedIngredientIds =
            ingredientDao.insertAll(*uniqueIngredientEntities.toTypedArray())

        // 2. Create a map of ingredient name (or another unique property) to its new ID
        //    This helps in quickly finding the ID of an ingredient when creating RecipeIngredient.
        val ingredientToIdMap = mutableMapOf<String, Long>()
        uniqueIngredientEntities.forEachIndexed { index, ingredientEntity ->
            // Assuming ingredientEntity has a 'name' property that was used for distinctness
            ingredientToIdMap[ingredientEntity.name] = insertedIngredientIds[index]
        }

        // 3. Insert recipes and then their associated ingredients
        list.forEach { recipe ->
            val ingredients = recipe.ingredients.map {
                it.copy(
                    ingredient = Ingredient(
                        id = ingredientToIdMap[it.ingredient.name]!!,
                        name = it.ingredient.name
                    )
                )
            }
            addRecipe(recipe.copy(id = 0, ingredients = ingredients, tags = emptyList()))
        }
    }

    override suspend fun populateSampleRecipes() = withContext(Dispatchers.IO) {
        try {
            val inputStream = applicationContext.resources.openRawResource(R.raw.sample_recipes)
            val recipeListType = object : TypeToken<List<Recipe>>() {}.type
            val reader = InputStreamReader(inputStream)
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
                    LocalDate.parse(json.asString) // parse date string
                })
                .create()
            val recipes = gson.fromJson<List<Recipe>>(reader, recipeListType)
            batchInsert(recipes)
        } catch (e: Exception) {
            Log.e("TAG", "populateSampleRecipes: ", e)
            e.printStackTrace()
        }
    }
}