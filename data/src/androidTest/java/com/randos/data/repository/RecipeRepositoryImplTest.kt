package com.randos.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.recipe1
import com.randos.data.utils.Utils.recipe2
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.RecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeRepositoryImplTest {

    private lateinit var database: MealMapDatabase
    private lateinit var recipeDao: RecipeDao
    private lateinit var ingredientDao: IngredientDao
    private lateinit var recipeIngredientDao: RecipeIngredientDao
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        database = getMealMapDatabase()
        recipeDao = database.recipeDao()
        ingredientDao = database.ingredientDao()
        recipeIngredientDao = database.recipeIngredientDao()
        recipeRepository = RecipeRepositoryImpl(recipeDao, ingredientDao, recipeIngredientDao)
        runTest {
            ingredientDao.insert(ingredient1.toEntity())
            ingredientDao.insert(ingredient2.toEntity())
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getRecipes_should_return_list_of_recipes() = runTest {
        // Given
        recipeRepository.addRecipe(recipe1)
        recipeRepository.addRecipe(recipe2)

        // When
        val recipes = recipeRepository.getRecipes()

        // Then
        assertEquals(2, recipes.size)
        assertEquals(recipe1.copy(ingredients = emptyList()), recipes[0])
    }

    @Test
    fun getRecipe_should_return_recipe_with_ingredients() = runTest {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        val result = recipeRepository.getRecipe(recipe1.id)

        // Then
        assertEquals(recipe1, result)
    }

    @Test
    fun addRecipe_should_insert_recipe_and_ingredients() = runTest {
        // When
        recipeRepository.addRecipe(recipe1)

        // Then
        val recipeEntity = recipeDao.get(recipe1.id)
        val ingredientEntities = recipeIngredientDao.get(recipe1.id)
        assertEquals(recipe1.toEntity(), recipeEntity)
        assertEquals(recipe1.ingredients.size, ingredientEntities.size)
    }

    @Test
    fun deleteRecipe_should_delete_recipe_and_ingredients() = runTest {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        recipeRepository.deleteRecipe(recipe1)

        // Then
        val recipes = recipeDao.getAll()
        val recipeIngredients = recipeIngredientDao.get(recipe1.id)
        assertEquals(0, recipes.size)
        assertEquals(0, recipeIngredients.size)
    }

    @Test
    fun updateRecipe_should_update_recipe_and_ingredients() = runTest() {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        recipeRepository.updateRecipe(
            recipe1.copy(
                ingredients = listOf(
                    RecipeIngredient(
                        ingredient1,
                        5.0
                    )
                ), instructions = listOf("NewStep")
            )
        )

        // Then
        val recipe = recipeDao.get(recipe1.id)
        val recipeIngredients = recipeIngredientDao.get(recipe!!.id)
        assertEquals(1, recipe.instructions.size)
        assertEquals("NewStep", recipe.instructions[0])
        assertEquals(1, recipeIngredients.size)
        assertEquals(ingredient1.id, recipeIngredients[0].ingredientId)
        assertEquals(5.0, recipeIngredients[0].quantity, 0.0)
    }
}