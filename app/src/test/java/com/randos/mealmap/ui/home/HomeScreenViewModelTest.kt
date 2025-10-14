package com.randos.mealmap.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.randos.domain.manager.SettingsManager
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.repository.MealRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.Day
import com.randos.domain.type.MealType
import com.randos.mealmap.MainDispatcherRule
import com.randos.mealmap.ui.getOrAwaitValue
import com.randos.mealmap.utils.Constants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mealRepository: MealRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var settingsManager: SettingsManager
    private lateinit var viewModel: HomeScreenViewModel

    // Common
    private val date = LocalDate.of(2025, 10, 9)
    val meal1 = Meal(
        recipes = Constants.recipes,
        date = date,
        type = MealType.BREAKFAST
    )
    val meal2 = Meal(
        recipes = Constants.recipes,
        date = date,
        type = MealType.LUNCH
    )

    val meal3 = Meal(
        recipes = Constants.recipes,
        date = date,
        type = MealType.DINNER
    )

    @Before
    fun setUp() {
        mealRepository = mockk()
        recipeRepository = mockk()
        settingsManager = mockk()
        viewModel = HomeScreenViewModel(
            mealRepository = mealRepository,
            recipeRepository = recipeRepository,
            settingsManager = settingsManager
        )

        mockkStatic(LocalDate::class)
        every { settingsManager.getFirstDayOfTheWeek() } returns Day.MONDAY
        every { LocalDate.now() } returns date
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getWeekPlan fetches meals for the current week`() = runTest {
        val meal4 = Meal(
            recipes = Constants.recipes,
            date = date.plusDays(2),
            type = MealType.DINNER
        )
        // Given
        val meals = listOf(meal1, meal2, meal3, meal4)
        val monday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        coEvery { mealRepository.getMealsForDateRange(any(), any()) } returns meals

        // When
        viewModel.getWeekPlan()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(listOf(meal1, meal2, meal3), state.mealMap[date])
        assertEquals(emptyList<Meal>(), state.mealMap[date.plusDays(1)])
        assertEquals(listOf(meal4), state.mealMap[date.plusDays(2)])
        assertEquals(monday, state.dateFrom)
        assertEquals(monday.plusDays(6), state.dateTo)

    }

    @Test
    fun `onIsSelectingWeekUpdate correctly updates state to true`() = runTest {
        // Given
        val isSelectingWeek = listOf(true, false)

        isSelectingWeek.forEach {
            // When
            viewModel.onIsSelectingWeekUpdate(it)
            val state = viewModel.state.getOrAwaitValue()
            // Then
            assertEquals(it, state.isSelectingWeek)
        }
    }

    @Test
    fun `onSelectedWeekTextUpdate fetches new week s data`() = runTest {
        // Given
        val meals = listOf(meal1, meal2, meal3)
        coEvery { mealRepository.getMealsForDateRange(any(), any()) } returns meals
        viewModel.getWeekPlan()
        advanceUntilIdle()
        assertEquals(listOf(meal1, meal2, meal3), viewModel.state.getOrAwaitValue().mealMap[date])

        // When
        viewModel.onSelectedWeekUpdate(Pair(1, "Next Week"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(Pair(1, "Next Week"), state.selectedWeek)
        assertTrue(state.mealMap.entries.map { it.value }.all { it.isEmpty() })
    }

    @Test
    fun `onCurrentMealEditingUpdate with non empty query triggers suggestions`() = runTest {
        // Given
        val recipes = Constants.recipes
        coEvery { recipeRepository.getRecipesLike(any()) } returns recipes

        // When
        viewModel.onCurrentMealEditingUpdate(Triple(date, MealType.BREAKFAST, "rec"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(Triple(date, MealType.BREAKFAST, "rec"), state.currentMealEditing)
        assertEquals(recipes, state.recipeSuggestions)
    }

    @Test
    fun `onCurrentMealEditingUpdate with empty query clears suggestions`() = runTest {
        // Given
        `onCurrentMealEditingUpdate with non empty query triggers suggestions`()

        // When
        viewModel.onCurrentMealEditingUpdate(Triple(date, MealType.BREAKFAST, ""))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(Triple(date, MealType.BREAKFAST, ""), state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onCurrentMealEditingUpdate with null value resets editing state`() = runTest {
        // Given
        `onCurrentMealEditingUpdate with non empty query triggers suggestions`()

        // When
        viewModel.onCurrentMealEditingUpdate(null)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(null, state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onAddMeal adds a recipe to a new meal`() = runTest {
        // Given
        val recipes = Constants.recipes
        coEvery { recipeRepository.addRecipe(any()) } returns 1
        coEvery { mealRepository.addMeal(any()) } returns 1

        // When
        viewModel.onAddMeal(recipes[0], MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        coVerify { mealRepository.addMeal(any()) }
        assertEquals(listOf(recipes[0]), state.mealMap[date]?.map { it.recipes[0] })
        assertNull(state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onAddMeal adds a recipe to an existing meal`() = runTest {
        // Given
        `onAddMeal adds a recipe to a new meal`()
        coEvery { mealRepository.updateMeal(any()) } returns Unit

        // When
        viewModel.onAddMeal(Constants.recipe.copy(id = 2), MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        val mealBreakfast = state.mealMap[date]?.find { it.type == MealType.BREAKFAST }

        coVerify { mealRepository.updateMeal(any()) }
        assertEquals(
            listOf(Constants.recipe, Constants.recipe.copy(id = 2)),
            mealBreakfast?.recipes
        )
        assertNull(state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onAddMeal does not add a duplicate recipe to a meal`() = runTest {
        // Given
        `onAddMeal adds a recipe to a new meal`()
        val initialState = viewModel.state.getOrAwaitValue()

        // When
        viewModel.onAddMeal(Constants.recipe, MealType.BREAKFAST, date)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(initialState, state)
        coVerify(exactly = 1) { mealRepository.addMeal(any()) } // Only one meal is added
        coVerify(exactly = 0) { mealRepository.updateMeal(any()) }
    }

    @Test
    fun `onAddNewRecipe creates a recipe and adds it to a new meal`() = runTest {
        // Given
        val recipe = slot<Recipe>()
        coEvery { recipeRepository.addRecipe(capture(recipe)) } returns 1
        coEvery { mealRepository.addMeal(any()) } returns 1

        // When
        viewModel.onAddNewRecipe("Recipe Title", MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        coVerify { recipeRepository.addRecipe(any()) }
        coVerify { mealRepository.addMeal(any()) }
        assertEquals("Recipe Title", recipe.captured.title)
        assertEquals(date, recipe.captured.dateCreated)
        assertEquals(
            listOf(Constants.recipe.title), state.mealMap[date]
                ?.find { it.type == MealType.BREAKFAST }?.recipes
                ?.map { it.title })
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
        assertNull(state.currentMealEditing)
    }

    @Test
    fun `onAddNewRecipe creates a recipe and adds it to an existing meal`() = runTest {
        // Given
        `onAddNewRecipe creates a recipe and adds it to a new meal`()
        coEvery { mealRepository.updateMeal(any()) } returns Unit

        // When
        viewModel.onAddNewRecipe("Recipe Title 2", MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        coVerify { mealRepository.updateMeal(any()) }
        assertEquals(
            listOf("Recipe Title", "Recipe Title 2"),
            state.mealMap[date]
                ?.find { it.type == MealType.BREAKFAST }?.recipes
                ?.map { it.title }
        )
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onRemoveMeal removes a recipe from a meal with multiple recipes`() = runTest {
        // Given
        `onAddMeal adds a recipe to an existing meal`()
        val meal =
            viewModel.state.getOrAwaitValue().mealMap[date]?.find { it.type == MealType.BREAKFAST }
        val recipes = meal?.recipes ?: emptyList()
        assertEquals(2, recipes.size)

        // When
        viewModel.onRemoveMeal(recipes[0], MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        coVerify { mealRepository.updateMeal(any()) }
        assertEquals(
            listOf(recipes[1]),
            state.mealMap[date]?.find { it.type == MealType.BREAKFAST }?.recipes
        )
        assertNull(state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }

    @Test
    fun `onRemoveMeal removes the last recipe and deletes the meal`() = runTest {
        // Given
        `onAddMeal adds a recipe to a new meal`()
        coEvery { mealRepository.updateMeal(any()) } returns Unit
        coEvery { mealRepository.deleteMeal(any()) } returns Unit
        val meal =
            viewModel.state.getOrAwaitValue().mealMap[date]?.find { it.type == MealType.BREAKFAST }
        val recipes = meal?.recipes ?: emptyList()
        assertEquals(1, recipes.size)

        // When
        viewModel.onRemoveMeal(recipes[0], MealType.BREAKFAST, date)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        coVerify { mealRepository.deleteMeal(any()) }
        assertEquals(emptyList<Meal>(), state.mealMap[date])
        assertNull(state.currentMealEditing)
        assertEquals(emptyList<Recipe>(), state.recipeSuggestions)
    }
}
