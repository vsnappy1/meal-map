package com.randos.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.database.MealMapDatabase
import com.randos.data.database.dao.IngredientDao
import com.randos.data.database.dao.RecipeDao
import com.randos.data.database.dao.RecipeIngredientDao
import com.randos.data.mapper.toEntity
import com.randos.data.utils.Utils
import com.randos.data.utils.Utils.getMealMapDatabase
import com.randos.data.utils.Utils.ingredient1
import com.randos.data.utils.Utils.ingredient2
import com.randos.data.utils.Utils.recipe1
import com.randos.data.utils.Utils.recipe2
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.IngredientUnit
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class RecipeRepositoryImplTest {

    private lateinit var database: MealMapDatabase
    private lateinit var recipeDao: RecipeDao
    private lateinit var ingredientDao: IngredientDao
    private lateinit var recipeIngredientDao: RecipeIngredientDao
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var applicationContext: Context
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        database = getMealMapDatabase()
        recipeDao = database.recipeDao()
        ingredientDao = database.ingredientDao()
        recipeIngredientDao = database.recipeIngredientDao()
        applicationContext = ApplicationProvider.getApplicationContext()
        recipeRepository = RecipeRepositoryImpl(
            recipeDao = recipeDao,
            ingredientDao = ingredientDao,
            recipeIngredientDao = recipeIngredientDao,
            applicationContext = applicationContext,
            dispatcher = dispatcher
        )
        runTest {
            ingredientDao.insert(ingredient1.toEntity())
            ingredientDao.insert(ingredient2.toEntity())
        }
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun getRecipes_should_return_list_of_recipes() = runTest(dispatcher) {
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
    fun getRecipe_should_return_recipe_with_ingredients() = runTest(dispatcher) {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        val result = recipeRepository.getRecipe(recipe1.id)

        // Then
        assertEquals(recipe1, result)
    }

    @Test
    fun addRecipe_should_insert_recipe_and_ingredients() = runTest(dispatcher) {
        // When
        recipeRepository.addRecipe(recipe1)

        // Then
        val recipeEntity = recipeDao.get(recipe1.id)
        val ingredientEntities = recipeIngredientDao.getByRecipeId(recipe1.id)
        assertEquals(recipe1.toEntity(), recipeEntity)
        assertEquals(recipe1.ingredients.size, ingredientEntities.size)
    }

    @Test
    fun deleteRecipe_should_delete_recipe_and_ingredients() = runTest(dispatcher) {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        recipeRepository.deleteRecipe(recipe1)

        // Then
        val recipes = recipeDao.getAll()
        val recipeIngredients = recipeIngredientDao.getByRecipeId(recipe1.id)
        assertEquals(0, recipes.size)
        assertEquals(0, recipeIngredients.size)
    }

    @Test
    fun updateRecipe_should_update_recipe_and_ingredients() = runTest(dispatcher) {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        recipeRepository.updateRecipe(
            recipe1.copy(
                ingredients = listOf(
                    RecipeIngredient(
                        ingredient1,
                        5.0,
                        IngredientUnit.TEASPOON
                    )
                ), instructions = listOf("NewStep")
            )
        )

        // Then
        val recipe = recipeDao.get(recipe1.id)
        val recipeIngredients = recipeIngredientDao.getByRecipeId(recipe!!.id)
        assertEquals(1, recipe.instructions.size)
        assertEquals("NewStep", recipe.instructions[0])
        assertEquals(1, recipeIngredients.size)
        assertEquals(ingredient1.id, recipeIngredients[0].ingredientId)
        assertEquals(5.0, recipeIngredients[0].quantity, 0.0)
    }

    @Test
    fun getRecipesLike_when_match_found_should_return_list_of_recipes() = runTest(dispatcher) {
        // Given
        val recipe1 = recipe1.copy(id = 1, title = "Pizza", ingredients = emptyList())
        val recipe2 = Utils.recipe1.copy(id = 2, title = "Pasta", ingredients = emptyList())
        val recipe3 = Utils.recipe1.copy(id = 3, title = "Salad", ingredients = emptyList())

        recipeRepository.addRecipe(recipe1)
        recipeRepository.addRecipe(recipe2)
        recipeRepository.addRecipe(recipe3)

        // When
        val recipes = recipeRepository.getRecipesLike("P")

        // Then
        assertEquals(2, recipes.size)
        assertEquals(recipe1, recipes[0])
        assertEquals(recipe2, recipes[1])
    }

    @Test
    fun getRecipesLike_when_match_no_found_should_return_empty_list() = runTest(dispatcher) {
        // Given
        val recipe1 = recipe1.copy(id = 1, title = "Pizza", ingredients = emptyList())
        val recipe2 = Utils.recipe1.copy(id = 2, title = "Pasta", ingredients = emptyList())
        val recipe3 = Utils.recipe1.copy(id = 3, title = "Salad", ingredients = emptyList())

        recipeRepository.addRecipe(recipe1)
        recipeRepository.addRecipe(recipe2)
        recipeRepository.addRecipe(recipe3)

        // When
        val recipes = recipeRepository.getRecipesLike("Chicken")

        // Then
        assertTrue(recipes.isEmpty())
    }

    @Test
    fun isEmpty_when_database_has_no_recipes_should_return_true() = runTest(dispatcher) {
        // When
        val isEmpty = recipeRepository.isEmpty()

        // Then
        assertTrue(isEmpty)
    }

    @Test
    fun isEmpty_when_database_has_one_or_more_recipes_should_return_false() = runTest(dispatcher) {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        val isEmpty = recipeRepository.isEmpty()

        // Then
        assertFalse(isEmpty)
    }

    @Test
    fun batchInsert_should_insert_all_recipes_and_ingredients() = runTest(dispatcher) {
        // Given
        val recipes = listOf(recipe1, recipe2)

        // When
        recipeRepository.batchInsert(recipes)

        // Then
        val insertedRecipes = recipeDao.getAll()
        val insertedIngredients = ingredientDao.getAll()
        val insertedRecipeIngredients = recipeIngredientDao.getAll()
        assertEquals(2, insertedRecipes.size)
        assertEquals(2, insertedIngredients.size)
        assertEquals(4, insertedRecipeIngredients.size)
    }

    @Test
    fun populateSampleRecipes_should_insert_sample_recipes() = runTest(dispatcher) {
        // When
        recipeRepository.populateSampleRecipes()

        // Then
        val insertedRecipes = recipeDao.getAll()
        assertEquals(14, insertedRecipes.size)
    }

    @Test
    fun getIngredientsForRecipe_should_return_ingredients_for_given_recipe() = runTest(dispatcher) {
        // Given
        recipeRepository.addRecipe(recipe1)

        // When
        val ingredients = recipeRepository.getIngredientsForRecipe(recipe1.id)

        // Then
        assertEquals(2, ingredients.size)
        assertEquals(ingredient1, ingredients[0].ingredient)
        assertEquals(ingredient2, ingredients[1].ingredient)
    }
}