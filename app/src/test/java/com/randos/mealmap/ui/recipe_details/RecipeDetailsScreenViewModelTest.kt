package com.randos.mealmap.ui.recipe_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.randos.domain.repository.RecipeRepository
import com.randos.mealmap.MainDispatcherRule
import com.randos.mealmap.ui.getOrAwaitValue
import com.randos.mealmap.utils.Utils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsScreenViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var recipesRepository: RecipeRepository
    private lateinit var recipeDetailsScreenViewModel: RecipeDetailsScreenViewModel
    private val recipe = Utils.recipe

    @Before
    fun setUp() {
        recipesRepository = mockk(relaxed = true)
        recipeDetailsScreenViewModel = RecipeDetailsScreenViewModel(recipesRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getState initial state`() {
        // Arrange
        // The ViewModel is already initialized in setUp()

        // Act
        val state = recipeDetailsScreenViewModel.state.value

        // Assert
        assertNull(state?.recipe)
    }

    @Test
    fun `getRecipeDetails updates state with fetched recipe`() = runTest {
        // Arrange
        coEvery { recipesRepository.getRecipe(any()) } returns recipe

        // Act
        recipeDetailsScreenViewModel.getRecipeDetails(1)
        advanceUntilIdle()

        // Assert
        val state = recipeDetailsScreenViewModel.state.getOrAwaitValue()
        assertEquals(recipe, state.recipe)
    }

    @Test
    fun `deleteRecipe successfully deletes and calls onDeleted`() = runTest {
        // Arrange
        coEvery { recipesRepository.getRecipe(any()) } returns recipe
        coEvery { recipesRepository.deleteRecipe(recipe) } just Runs

        recipeDetailsScreenViewModel.getRecipeDetails(1)

        val onDeletedCallback = mockk<() -> Unit>(relaxed = true)

        // Act
        recipeDetailsScreenViewModel.deleteRecipe(onDeletedCallback)
        advanceUntilIdle()

        // Assert
        coVerify { recipesRepository.deleteRecipe(recipe) }
        verify { onDeletedCallback() }
    }

    @Test
    fun `deleteRecipe when no recipe is loaded`() = runTest {
        // Arrange
        assertNull(recipeDetailsScreenViewModel.state.value?.recipe) // Verify initial state
        val onDeletedCallback = mockk<() -> Unit>(relaxed = true)

        // Act
        recipeDetailsScreenViewModel.deleteRecipe(onDeletedCallback)

        // Assert
        coVerify(exactly = 0) { recipesRepository.deleteRecipe(any()) }
        verify(exactly = 0) { onDeletedCallback() }
    }

    @Test
    fun `deleteRecipe onDeleted callback execution`() = runTest {
        // Arrange
        coEvery { recipesRepository.getRecipe(any()) } returns recipe
        coEvery { recipesRepository.deleteRecipe(recipe) } just Runs
        recipeDetailsScreenViewModel.getRecipeDetails(1)

        val onDeletedCallback = mockk<() -> Unit>(relaxed = true)

        // Act
        recipeDetailsScreenViewModel.deleteRecipe(onDeletedCallback)
        advanceUntilIdle()

        // Assert
        verify(exactly = 1) { onDeletedCallback() }
    }
}