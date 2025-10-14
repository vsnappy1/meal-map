package com.randos.mealmap.ui.grocery_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.randos.domain.manager.GroceryListManager
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.Ingredient
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.MealRepository
import com.randos.domain.type.Day
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.MainDispatcherRule
import com.randos.mealmap.ui.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryListScreenViewModelTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mealRepository: MealRepository
    private lateinit var settingsManager: SettingsManager
    private lateinit var groceryListManager: GroceryListManager
    private lateinit var viewModel: GroceryListScreenViewModel

    // Common
    val recipeIngredients = listOf(
        RecipeIngredient(Ingredient(name = "Potato"), 100.0, IngredientUnit.GRAM),
        RecipeIngredient(Ingredient(name = "Potato"), 1.0, IngredientUnit.KILOGRAM),
        RecipeIngredient(Ingredient(name = "Potato"), 1.0, null),
        RecipeIngredient(Ingredient(name = "Tomato"), 200.0, IngredientUnit.GRAM),
        RecipeIngredient(Ingredient(name = "Onion"), 50.0, IngredientUnit.GRAM),
        RecipeIngredient(Ingredient(name = "Garlic"), 10.0, IngredientUnit.GRAM),
        RecipeIngredient(Ingredient(name = "Orange Juice"), 1.0, IngredientUnit.LITER),
        RecipeIngredient(Ingredient(name = "Orange Juice"), 1.0, IngredientUnit.ML),
        RecipeIngredient(Ingredient(name = "Baking Soda"), 2.0, IngredientUnit.TEASPOON),
        RecipeIngredient(Ingredient(name = "Baking Powder"), 0.25, IngredientUnit.TABLESPOON),
    )

    @Before
    fun setUp() {
        mealRepository = mockk()
        settingsManager = mockk()
        groceryListManager = mockk()
        viewModel = GroceryListScreenViewModel(
            mealRepository = mealRepository,
            settingsManager = settingsManager,
            groceryListManager = groceryListManager
        )
        runBlocking {
            coEvery {
                mealRepository.getRecipeIngredientsForDateRange(
                    any(),
                    any()
                )
            } returns recipeIngredients
            coEvery { groceryListManager.getCheckedGroceryIngredientsNameForWeek(any()) } returns emptySet()
            coEvery { settingsManager.getFirstDayOfTheWeek() } returns Day.MONDAY
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `getGroceryIngredients successful retrieval`() = runTest {
        // When
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val potato = state?.groceryIngredients?.filter { it.name == "Potato" }
        assertEquals(7, state?.groceryIngredients?.size)
        assertEquals(1, potato?.size)
        assertEquals(2, potato?.first()?.amountsByUnit?.size)
        assertTrue(
            potato?.first()?.amountsByUnit?.contains(Pair(IngredientUnit.KILOGRAM, 1.1)) ?: false
        )
        assertTrue(potato?.first()?.amountsByUnit?.contains(Pair(null, 1.0)) ?: false)
        assertTrue(state?.groceryIngredients?.all { !it.isChecked } ?: false)
    }

    @Test
    fun `getGroceryIngredients with no recipes in date range`() = runTest {
        // Given
        coEvery {
            mealRepository.getRecipeIngredientsForDateRange(
                any(),
                any()
            )
        } returns emptyList()

        // When
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(0, state?.groceryIngredients?.size)
    }

    @Test
    fun `getGroceryIngredients with some ingredients already checked`() = runTest {
        // Given
        val checkedIngredients = setOf("Potato", "Onion")
        coEvery { groceryListManager.getCheckedGroceryIngredientsNameForWeek(any()) } returns checkedIngredients

        // When
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val potato = state?.groceryIngredients?.find { it.name == "Potato" }
        val onion = state?.groceryIngredients?.find { it.name == "Onion" }
        assertEquals(7, state?.groceryIngredients?.size)
        assertEquals(true, potato?.isChecked)
        assertEquals(true, onion?.isChecked)
    }

    @Test
    fun `getGroceryIngredients with all ingredients checked`() = runTest {
        // Given
        val checkedIngredients =
            recipeIngredients.distinctBy { it.ingredient.name }.map { it.ingredient.name }.toSet()
        coEvery { groceryListManager.getCheckedGroceryIngredientsNameForWeek(any()) } returns checkedIngredients

        // When
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(7, state?.groceryIngredients?.size)
        assertTrue(state?.groceryIngredients?.all { it.isChecked } ?: false)
    }

    @Test
    fun `getGroceryIngredients with ingredients having multiple units`() = runTest {
        // When
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val potato = state?.groceryIngredients?.find { it.name == "Potato" }
        assertTrue(potato?.amountsByUnit?.contains(Pair(IngredientUnit.KILOGRAM, 1.1)) ?: false)
        assertTrue(potato?.amountsByUnit?.contains(Pair(null, 1.0)) ?: false)
    }

    @Test
    fun `onIsSelectingWeekUpdate to true`() = runTest {
        listOf(true, false).forEach {
            // When
            viewModel.onIsSelectingWeekUpdate(it)

            // Then
            assertEquals(it, viewModel.state.getOrAwaitValue().isSelectingWeek)
        }
    }

    @Test
    fun `onSelectedWeekUpdate successful update`() = runTest {
        // When
        viewModel.onSelectedWeekUpdate(Pair(0, "This Week"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(7, state?.groceryIngredients?.size)
    }

    @Test
    fun `onSelectedWeekUpdate to a future week`() = runTest {
        // Given
        coEvery {
            mealRepository.getRecipeIngredientsForDateRange(
                any(),
                any()
            )
        } returns emptyList()

        // When
        viewModel.onSelectedWeekUpdate(Pair(1, "Next Week"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(0, state?.groceryIngredients?.size)
    }

    @Test
    fun `onIngredientCheckedUpdate to checked`() = runTest {
        // Given
        coEvery { groceryListManager.markIngredientAsChecked(any(), any()) } returns Unit
        viewModel.getGroceryIngredients()
        advanceUntilIdle()

        // When
        viewModel.onIngredientCheckedUpdate(0, true)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(true, state?.groceryIngredients?.get(0)?.isChecked)
        coVerify { groceryListManager.markIngredientAsChecked(any(), any()) }
    }

    @Test
    fun `onIngredientCheckedUpdate to unchecked`() = runTest {
        // Given
        `onIngredientCheckedUpdate to checked`()
        coEvery { groceryListManager.markIngredientAsUnchecked(any(), any()) } returns Unit

        // When
        viewModel.onIngredientCheckedUpdate(0, false)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(false, state?.groceryIngredients?.get(0)?.isChecked)
        coVerify { groceryListManager.markIngredientAsUnchecked(any(), any()) }
    }
}