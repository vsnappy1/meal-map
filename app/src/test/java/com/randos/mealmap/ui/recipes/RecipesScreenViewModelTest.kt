package com.randos.mealmap.ui.recipes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesSort
import com.randos.domain.type.SortOrder
import com.randos.mealmap.MainDispatcherRule
import com.randos.mealmap.ui.getOrAwaitValue
import com.randos.mealmap.utils.Utils
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesScreenViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var recipesRepository: RecipeRepository
    private lateinit var viewModel: RecipesScreenViewModel
    val recipe = Utils.recipe

    @Before
    fun setUp() {
        recipesRepository = mockk(relaxed = true)
        viewModel = RecipesScreenViewModel(recipesRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getState when initial state is observed it should have empty recipes list, null filter, null sort, default sort order, and empty search text `() {
        val initialState = viewModel.state.getOrAwaitValue()
        assertTrue(initialState.recipes.isEmpty())
        assertNull(initialState.filter)
        assertNull(initialState.sort)
        assertEquals("", initialState.searchText)
        assertEquals(SortOrder.ASCENDING, initialState.sortOrder)
    }

    @Test
    fun `onSearchTextChange with empty text should not filter recipes by search text`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "First Recipe"),
            recipe.copy(id = 2, title = "Second Recipe")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onSearchTextChange("")
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals("", state.searchText)
        assertEquals(2, state.recipes.size)
        assertEquals(recipes, state.recipes)
    }

    @Test
    fun `onSearchTextChange with non matching text should return empty list`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "Apple Pie"),
            recipe.copy(id = 2, title = "Banana Bread")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onSearchTextChange("Chocolate Cake")
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals("Chocolate Cake", state.searchText)
        assertTrue(state.recipes.isEmpty())
    }

    @Test
    fun `onSearchTextChange with special characters should return list of matching recipes`() =
        runTest {
            val recipes = listOf(
                recipe.copy(id = 1, title = "Salad (light)"),
                recipe.copy(id = 2, title = "Burger & Fries")
            )
            coEvery { recipesRepository.getRecipes() } returns recipes
            viewModel.getRecipes()
            advanceUntilIdle()

            viewModel.onSearchTextChange("(light)")
            advanceUntilIdle()

            val state = viewModel.state.getOrAwaitValue()
            assertEquals("(light)", state.searchText)
            assertEquals(1, state.recipes.size)
            assertEquals("Salad (light)", state.recipes[0].title)
        }

    @Test
    fun `onSearchTextChange case insensitivity should return list of matching recipes`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "Pasta Carbonara"),
            recipe.copy(id = 2, title = "pasta primavera")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onSearchTextChange("PASTA")
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals("PASTA", state.searchText)
        assertEquals(2, state.recipes.size)
    }

    @Test
    fun `onFilterChange with null filter`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, tags = listOf(RecipeTag.QUICK)),
            recipe.copy(id = 2, tags = listOf(RecipeTag.CHICKEN))
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Apply a filter first
        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()

        // Then clear it
        viewModel.onFilterChange(null)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.filter)
        assertEquals(2, state.recipes.size)
        assertEquals(recipes, state.recipes)
    }

    @Test
    fun `onFilterChange with non matching filter`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, tags = listOf(RecipeTag.QUICK)),
            recipe.copy(id = 2, tags = listOf(RecipeTag.CHICKEN))
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onFilterChange(RecipeTag.VEGETABLE)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipeTag.VEGETABLE, state.filter)
        assertTrue(state.recipes.isEmpty())
    }

    @Test
    fun `onFilterChange with matching filter`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, tags = listOf(RecipeTag.QUICK)),
            recipe.copy(id = 2, tags = listOf(RecipeTag.CHICKEN))
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipeTag.QUICK, state.filter)
        assertEquals(RecipeTag.QUICK, state.recipes[0].tags[0])
        assertEquals(1, state.recipes.size)
    }

    @Test
    fun `onSortChange with null sort`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "C"),
            recipe.copy(id = 2, title = "A"),
            recipe.copy(id = 3, title = "B")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Apply a sort first
        viewModel.onSortChange(RecipesSort.TITLE)
        advanceUntilIdle()
        assertEquals(
            listOf("A", "B", "C"),
            viewModel.state.getOrAwaitValue().recipes.map { it.title })

        // Then clear it
        viewModel.onSortChange(null)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.sort)
        // Order should revert to original
        assertEquals(listOf("C", "A", "B"), state.recipes.map { it.title })
    }

    @Test
    fun `onSortChange with different sort options`() = runTest {
        val date = LocalDate.now()
        val recipes = listOf(
            recipe.copy(
                id = 1,
                title = "C",
                dateCreated = date,
                calories = 300,
                heaviness = RecipeHeaviness.HEAVY
            ),
            recipe.copy(
                id = 2,
                title = "A",
                dateCreated = date.minusDays(2),
                calories = 100,
                heaviness = RecipeHeaviness.LIGHT
            ),
            recipe.copy(
                id = 3,
                title = "B",
                dateCreated = date.minusDays(1),
                calories = 200,
                heaviness = RecipeHeaviness.MEDIUM
            )
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Sort by TITLE
        viewModel.onSortChange(RecipesSort.TITLE)
        advanceUntilIdle()
        var state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipesSort.TITLE, state.sort)
        assertEquals(listOf("A", "B", "C"), state.recipes.map { it.title })

        // Sort by CREATED_DATE
        viewModel.onSortChange(RecipesSort.CREATED_DATE)
        advanceUntilIdle()
        state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipesSort.CREATED_DATE, state.sort)
        assertEquals(
            listOf(date.minusDays(2), date.minusDays(1), date),
            state.recipes.map { it.dateCreated })

        // Sort by CALORIES
        viewModel.onSortChange(RecipesSort.CALORIES)
        advanceUntilIdle()
        state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipesSort.CALORIES, state.sort)
        assertEquals(listOf(100, 200, 300), state.recipes.map { it.calories })

        // Sort by HEAVINESS
        viewModel.onSortChange(RecipesSort.HEAVINESS)
        advanceUntilIdle()
        state = viewModel.state.getOrAwaitValue()
        assertEquals(RecipesSort.HEAVINESS, state.sort)
        assertEquals(
            listOf(RecipeHeaviness.LIGHT, RecipeHeaviness.MEDIUM, RecipeHeaviness.HEAVY),
            state.recipes.map { it.heaviness })
    }

    @Test
    fun `onSortOrderChange to DESCENDING`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "C"),
            recipe.copy(id = 2, title = "A"),
            recipe.copy(id = 3, title = "B")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onSortChange(RecipesSort.TITLE)
        advanceUntilIdle()

        viewModel.onSortOrderChange(SortOrder.DESCENDING)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(SortOrder.DESCENDING, state.sortOrder)
        assertEquals(RecipesSort.TITLE, state.sort)
        assertEquals(listOf("C", "B", "A"), state.recipes.map { it.title })
    }

    @Test
    fun `onSortOrderChange to ASCENDING`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "C"),
            recipe.copy(id = 2, title = "A"),
            recipe.copy(id = 3, title = "B")
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // First set to DESCENDING
        viewModel.onSortChange(RecipesSort.TITLE)
        advanceUntilIdle()
        viewModel.onSortOrderChange(SortOrder.DESCENDING)
        advanceUntilIdle()
        assertEquals(
            listOf("C", "B", "A"),
            viewModel.state.getOrAwaitValue().recipes.map { it.title })

        // Then change back to ASCENDING
        viewModel.onSortOrderChange(SortOrder.ASCENDING)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(SortOrder.ASCENDING, state.sortOrder)
        assertEquals(RecipesSort.TITLE, state.sort)
        assertEquals(listOf("A", "B", "C"), state.recipes.map { it.title })
    }

    @Test
    fun `Interaction  Search  then Filter  then Sort  then change Sort Order`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "Chicken Pasta", tags = listOf(RecipeTag.QUICK, RecipeTag.CHICKEN), calories = 500),
            recipe.copy(id = 2, title = "Beef Pasta", tags = listOf(RecipeTag.QUICK), calories = 700),
            recipe.copy(id = 3, title = "Chicken Salad", tags = listOf(RecipeTag.CHICKEN), calories = 300),
            recipe.copy(id = 4, title = "Vegetable Pasta", tags = listOf(RecipeTag.VEGETABLE), calories = 400)
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // 1. Search for text
        viewModel.onSearchTextChange("Pasta")
        advanceUntilIdle()
        assertEquals(3, viewModel.state.getOrAwaitValue().recipes.size)
        assertEquals(listOf("Chicken Pasta", "Beef Pasta", "Vegetable Pasta"), viewModel.state.getOrAwaitValue().recipes.map { it.title })

        // 2. Apply a filter
        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()
        assertEquals(2, viewModel.state.getOrAwaitValue().recipes.size)
        assertEquals(listOf("Chicken Pasta", "Beef Pasta"), viewModel.state.getOrAwaitValue().recipes.map { it.title })

        // 3. Apply a sort
        viewModel.onSortChange(RecipesSort.CALORIES)
        advanceUntilIdle()
        assertEquals(listOf("Chicken Pasta", "Beef Pasta"), viewModel.state.getOrAwaitValue().recipes.map { it.title })
        assertEquals(listOf(500, 700), viewModel.state.getOrAwaitValue().recipes.map { it.calories })

        // 4. Change sort order
        viewModel.onSortOrderChange(SortOrder.DESCENDING)
        advanceUntilIdle()
        val finalState = viewModel.state.getOrAwaitValue()
        assertEquals("Pasta", finalState.searchText)
        assertEquals(RecipeTag.QUICK, finalState.filter)
        assertEquals(RecipesSort.CALORIES, finalState.sort)
        assertEquals(SortOrder.DESCENDING, finalState.sortOrder)
        assertEquals(listOf("Beef Pasta", "Chicken Pasta"), finalState.recipes.map { it.title })
        assertEquals(listOf(700, 500), finalState.recipes.map { it.calories })
    }

    @Test
    fun `Interaction  Clear search text after filtering and sorting`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "A", tags = listOf(RecipeTag.QUICK), calories = 200),
            recipe.copy(id = 2, title = "B", tags = listOf(RecipeTag.QUICK), calories = 100),
            recipe.copy(id = 3, title = "C", tags = listOf(RecipeTag.CHICKEN), calories = 300)
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Apply search, filter, and sort
        viewModel.onSearchTextChange("A")
        advanceUntilIdle()
        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()
        viewModel.onSortChange(RecipesSort.CALORIES)
        advanceUntilIdle()

        assertEquals(1, viewModel.state.getOrAwaitValue().recipes.size) // Only "A" matches

        // Clear search text
        viewModel.onSearchTextChange("")
        advanceUntilIdle()

        // Should show all items matching filter and sort
        val state = viewModel.state.getOrAwaitValue()
        assertEquals("", state.searchText)
        assertEquals(RecipeTag.QUICK, state.filter)
        assertEquals(RecipesSort.CALORIES, state.sort)
        assertEquals(2, state.recipes.size)
        assertEquals(listOf("B", "A"), state.recipes.map { it.title }) // Sorted by calories ASC
        assertEquals(listOf(100, 200), state.recipes.map { it.calories })
    }

    @Test
    fun `Interaction  Clear filter after searching and sorting`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "Apple Pie", tags = listOf(RecipeTag.QUICK), calories = 300),
            recipe.copy(id = 2, title = "Apple Crumble", tags = listOf(RecipeTag.VEGETABLE), calories = 400),
            recipe.copy(id = 3, title = "Banana Bread", tags = listOf(RecipeTag.QUICK), calories = 200)
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Apply filter, search, and sort
        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()
        viewModel.onSearchTextChange("Apple")
        advanceUntilIdle()
        viewModel.onSortChange(RecipesSort.CALORIES)
        advanceUntilIdle()

        assertEquals(1, viewModel.state.getOrAwaitValue().recipes.size) // Only "Apple Pie" matches

        // Clear filter
        viewModel.onFilterChange(null)
        advanceUntilIdle()

        // Should show all items matching search and sort
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.filter)
        assertEquals("Apple", state.searchText)
        assertEquals(RecipesSort.CALORIES, state.sort)
        assertEquals(2, state.recipes.size)
        assertEquals(listOf("Apple Pie", "Apple Crumble"), state.recipes.map { it.title }) // Sorted by calories ASC
        assertEquals(listOf(300, 400), state.recipes.map { it.calories })
    }

    @Test
    fun `Recipes with empty tags list for filtering`() = runTest {
        val recipes = listOf(
            recipe.copy(id = 1, title = "Recipe With Tag", tags = listOf(RecipeTag.QUICK)),
            recipe.copy(id = 2, title = "Recipe With Empty Tags", tags = emptyList())
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        viewModel.onFilterChange(RecipeTag.QUICK)
        advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipes.size)
        assertEquals("Recipe With Tag", state.recipes[0].title)
    }

    @Test
    fun `Recipes with null or default values for sortable properties`() = runTest {
        // If dateCreated, calories, or heaviness can be null or have default values (e.g., 0 for calories),
        // verify the sorting behavior in such cases, especially for stability if multiple items have the same sort key value.
        val recipes = listOf(
            recipe.copy(id = 1, title = "C", calories = 200, dateCreated = LocalDate.now()), // default date
            recipe.copy(id = 2, title = "A", calories = 0, dateCreated = LocalDate.now().minusDays(2)),   // 0 calories
            recipe.copy(id = 3, title = "B", calories = 200, dateCreated = LocalDate.now().minusDays(1))  // same calories as C
        )
        coEvery { recipesRepository.getRecipes() } returns recipes
        viewModel.getRecipes()
        advanceUntilIdle()

        // Sort by CALORIES, ascending
        viewModel.onSortChange(RecipesSort.CALORIES)
        advanceUntilIdle()

        var state = viewModel.state.getOrAwaitValue()
        assertEquals(
            listOf("A", "C", "B"),
            state.recipes.map { it.title }
        )
        assertEquals(
            listOf(0, 200, 200),
            state.recipes.map { it.calories }
        )

        // Verify stability by changing sort order
        viewModel.onSortOrderChange(SortOrder.DESCENDING)
        advanceUntilIdle()
        state = viewModel.state.getOrAwaitValue()
        assertEquals(
            listOf("B", "C", "A"),
            state.recipes.map { it.title }
        )
        assertEquals(
            listOf(200, 200, 0),
            state.recipes.map { it.calories }
        )
    }
}
