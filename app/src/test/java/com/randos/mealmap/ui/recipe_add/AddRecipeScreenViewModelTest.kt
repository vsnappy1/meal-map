package com.randos.mealmap.ui.recipe_add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.repository.IngredientRepository
import com.randos.domain.repository.RecipeRepository
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.mealmap.MainDispatcherRule
import com.randos.mealmap.ui.getOrAwaitValue
import com.randos.mealmap.utils.Constants.RECIPE_COOKING_TIME_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_DESCRIPTION_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_ERROR_MESSAGE_SHOWN_DURATION
import com.randos.mealmap.utils.Constants.RECIPE_INGREDIENTS_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_INSTRUCTIONS_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_PREPARATION_TIME_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_TITLE_MAX_LENGTH
import com.randos.mealmap.utils.Constants.RECIPE_TOTAL_CALORIES_MAX_LENGTH
import com.randos.mealmap.utils.Utils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddRecipeScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var recipesRepository: RecipeRepository
    private lateinit var ingredientsRepository: IngredientRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: AddRecipeScreenViewModel

    private val recipe = Utils.recipe

    @Before
    fun setUp() {
        recipesRepository = mockk(relaxed = true)
        ingredientsRepository = mockk(relaxed = true)
        savedStateHandle = mockk(relaxed = true)

        every { savedStateHandle.get<Long>("id") } returns null
        initializeViewModel()
    }

    private fun initializeViewModel() {
        viewModel = AddRecipeScreenViewModel(
            recipesRepository = recipesRepository,
            ingredientsRepository = ingredientsRepository,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getState when recipeId is provided should return associated recipe`() = runTest {
        // Given
        val recipeId = 1L
        every { savedStateHandle.get<Long?>("id") } returns recipeId // Specific setup for this test
        coEvery { recipesRepository.getRecipe(recipeId) } returns recipe

        // When
        initializeViewModel()
        assertEquals(true, viewModel.state.getOrAwaitValue().isLoading)
        advanceUntilIdle()

        // Then
        coVerify { recipesRepository.getRecipe(recipeId) }
        assertEquals(recipe, viewModel.state.getOrAwaitValue().recipe)
        assertEquals(false, viewModel.state.getOrAwaitValue().isLoading)
    }

    @Test
    fun `getState when recipeId is null should return empty recipe`() = runTest {
        // When
        initializeViewModel()
        assertEquals(false, viewModel.state.getOrAwaitValue().isLoading)
        advanceUntilIdle()

        // Then
        assertEquals(0L, viewModel.state.getOrAwaitValue().recipe.id)
        assertEquals(false, viewModel.state.getOrAwaitValue().isLoading)
    }

    @Test
    fun `onSave when recipeId is null should add new recipe`() = runTest {
        // When
        viewModel.onSave(onSaved = {})
        advanceUntilIdle()

        // Then
        coVerify { recipesRepository.addRecipe(any()) }
    }

    @Test
    fun `onSave when recipeId is null should update recipe`() = runTest {
        // Given
        val recipeId = 1L
        every { savedStateHandle.get<Long?>("id") } returns recipeId // Specific setup for this test
        coEvery { recipesRepository.getRecipe(recipeId) } returns recipe

        // When
        initializeViewModel()
        viewModel.onSave(onSaved = {})
        advanceUntilIdle()

        // Then
        coVerify { recipesRepository.updateRecipe(any()) }
    }

    @Test
    fun `onSave when image path is not null should update recipe with updated image path`() =
        runTest {
            // Given
            val imagePath = "imagePath"
            val recipeSlot = slot<Recipe>()

            // When
            viewModel.onSave(imagePath = imagePath, onSaved = {})
            advanceUntilIdle()

            // Then
            coVerify { recipesRepository.addRecipe(capture(recipeSlot)) }
            assertEquals(imagePath, recipeSlot.captured.imagePath)
        }

    @Test
    fun `onSave when image path is null imagePath remains unchanged`() = runTest {
        // Given
        val recipeSlot = slot<Recipe>()

        // When
        viewModel.onSave(onSaved = {})
        advanceUntilIdle()

        // Then
        coVerify { recipesRepository.addRecipe(capture(recipeSlot)) }
        assertEquals(null, recipeSlot.captured.imagePath)
    }

    @Test
    fun `onSave when save operation is successful should invoke onSaved callback`() = runTest {
        // Given
        val onSaved = mockk<() -> Unit>(relaxed = true)

        // When
        viewModel.onSave(onSaved = onSaved)
        advanceUntilIdle()

        // Then
        verify { onSaved() }
    }

    @Test
    fun `onIngredientTextChange when text length exceeds max length should not update state`() =
        runTest {
            // Given
            val text = "a".repeat(RECIPE_INGREDIENTS_MAX_LENGTH + 1)

            // When
            viewModel.onIngredientTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { ingredientsRepository.getIngredientsLike(text) }
            assertEquals("", viewModel.state.getOrAwaitValue().currentIngredientText)
        }

    @Test
    fun `onIngredientTextChange when text is empty should update currentIngredientText and clear ingredientSuggestions`() =
        runTest {
            // Given
            val text = ""

            // When
            viewModel.onIngredientTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { ingredientsRepository.getIngredientsLike(text) }
            assertEquals("", viewModel.state.getOrAwaitValue().currentIngredientText)
            assertEquals(
                emptyList<Ingredient>(),
                viewModel.state.getOrAwaitValue().ingredientSuggestions
            )
        }

    @Test
    fun `onIngredientTextChange when text length is less than max length should update currentIngredientText and call findSuggestion`() =
        runTest {
            // Given
            val text = "abc"

            // When
            viewModel.onIngredientTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify { ingredientsRepository.getIngredientsLike(text) }
            assertEquals(text, viewModel.state.getOrAwaitValue().currentIngredientText)
        }

    @Test
    fun `onIngredientEditTextChanged when text length exceeds max length should not update state`() =
        runTest {
            // Given
            val text = "a".repeat(RECIPE_INGREDIENTS_MAX_LENGTH + 1)

            // When
            viewModel.onIngredientEditTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { ingredientsRepository.getIngredientsLike(text) }
            assertEquals("", viewModel.state.getOrAwaitValue().editIngredientText)
        }

    @Test
    fun `onIngredientEditTextChange when text is empty should update currentIngredientText and clear ingredientSuggestions`() =
        runTest {
            // Given
            val text = ""

            // When
            viewModel.onIngredientEditTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { ingredientsRepository.getIngredientsLike(text) }
            assertEquals("", viewModel.state.getOrAwaitValue().editIngredientText)
            assertEquals(
                emptyList<Ingredient>(),
                viewModel.state.getOrAwaitValue().ingredientSuggestions
            )
        }

    @Test
    fun `onIngredientEditTextChange when text length is less than max length should update currentIngredientText and call findSuggestion`() =
        runTest {
            // Given
            val text = "abc"

            // When
            viewModel.onIngredientEditTextChange(text)
            advanceUntilIdle()

            // Then
            coVerify { ingredientsRepository.getIngredientsLike(text) }
            assertEquals(text, viewModel.state.getOrAwaitValue().editIngredientText)
        }

    @Test
    fun `onIngredientIsEditingChange when value is true should update editIngredientIndex and editIngredientText`() =
        runTest {
            // Given
            val text = "Ingredient 1"
            val index = 0
            coEvery { ingredientsRepository.addIngredient(any()) } returns Ingredient(name = text)
            viewModel.onIngredientAdd(text)
            advanceUntilIdle()

            // When
            viewModel.onIngredientIsEditingChange(index, true)
            advanceUntilIdle()

            // Then
            assertEquals(index, viewModel.state.getOrAwaitValue().editIngredientIndex)
            assertEquals(text, viewModel.state.getOrAwaitValue().editIngredientText)
        }

    @Test
    fun `onIngredientIsEditingChange when value is false should update editIngredientIndex to null and editIngredientText to empty`() =
        runTest {
            // Given
            coEvery { ingredientsRepository.addIngredient(any()) } returns mockk(relaxed = true)
            viewModel.onIngredientAdd("text")
            advanceUntilIdle()

            // When
            viewModel.onIngredientIsEditingChange(0, false)
            advanceUntilIdle()

            // Then
            assertEquals(null, viewModel.state.getOrAwaitValue().editIngredientIndex)
            assertEquals("", viewModel.state.getOrAwaitValue().editIngredientText)
        }

    @Test
    fun `onIngredientAdd new ingredient should add to recipe, repository and update state`() =
        runTest {
            // Given
            val ingredientText = "New Ingredient"
            val newIngredient = Ingredient(name = ingredientText)
            val ingredientSlot = slot<Ingredient>()
            coEvery { ingredientsRepository.addIngredient(capture(ingredientSlot)) } returns newIngredient

            // When
            viewModel.onIngredientAdd(ingredientText)
            advanceUntilIdle()

            // Then
            coVerify { ingredientsRepository.addIngredient(Ingredient(name = ingredientText)) }
            assertEquals(ingredientText, ingredientSlot.captured.name)
            val state = viewModel.state.getOrAwaitValue()
            assertEquals(1, state.recipe.ingredients.size)
            assertEquals(ingredientText, state.recipe.ingredients.first().ingredient.name)
            assertEquals("", state.currentIngredientText)
            assertEquals(emptyList<Ingredient>(), state.ingredientSuggestions)
        }

    @Test
    fun `onIngredientAdd duplicate ingredient should not add and show error`() = runTest {
        // Given
        val ingredientText = "Duplicate Ingredient"
        val existingIngredient = Ingredient(name = ingredientText)
        coEvery { ingredientsRepository.addIngredient(any()) } returns existingIngredient

        // First add
        viewModel.onIngredientAdd(ingredientText)
        advanceUntilIdle()

        // When - trying to add a duplicate
        viewModel.onIngredientAdd(ingredientText)
        advanceTimeBy(RECIPE_ERROR_MESSAGE_SHOWN_DURATION - 100)

        // Then
        coVerify(exactly = 1) { ingredientsRepository.addIngredient(Ingredient(name = ingredientText)) }
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.ingredients.size)
        assertNotNull(state.errorMessage)

        // Verify error message is cleared after a delay
        advanceUntilIdle()
        assertNull(viewModel.state.getOrAwaitValue().errorMessage)
    }

    @Test
    fun `onIngredientAdd with leading and trailing spaces should trim text`() = runTest {
        // Given
        val ingredientTextWithSpaces = "  Trimmed Ingredient  "
        val trimmedIngredientText = "Trimmed Ingredient"
        val newIngredient = Ingredient(name = trimmedIngredientText)
        val ingredientSlot = slot<Ingredient>()
        coEvery { ingredientsRepository.addIngredient(capture(ingredientSlot)) } returns newIngredient

        // When
        viewModel.onIngredientAdd(ingredientTextWithSpaces)
        advanceUntilIdle()

        // Then
        coVerify { ingredientsRepository.addIngredient(Ingredient(name = trimmedIngredientText)) }
        assertEquals(trimmedIngredientText, ingredientSlot.captured.name)
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.ingredients.size)
        assertEquals(trimmedIngredientText, state.recipe.ingredients.first().ingredient.name)
    }

    @Test
    fun `onIngredientUpdate existing ingredient`() = runTest {
        // Given
        val indexToUpdate = 0
        val initialIngredient = Ingredient(1, "Old Ingredient")
        val updatedIngredientName = "New Ingredient"
        val updatedIngredient = Ingredient(2, updatedIngredientName)

        // Add initial ingredient
        coEvery { ingredientsRepository.addIngredient(any()) } returns initialIngredient
        viewModel.onIngredientAdd(initialIngredient.name)
        advanceUntilIdle()

        // Mock the repository to return the new ingredient when adding
        coEvery { ingredientsRepository.addIngredient(Ingredient(name = updatedIngredientName)) } returns updatedIngredient

        // When
        viewModel.onIngredientUpdate(index = indexToUpdate, text = updatedIngredientName)
        advanceUntilIdle()

        // Then
        coVerify { ingredientsRepository.addIngredient(Ingredient(name = updatedIngredientName)) }
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.ingredients.size)
        assertEquals(updatedIngredientName, state.recipe.ingredients[indexToUpdate].ingredient.name)
        assertEquals(updatedIngredient.id, state.recipe.ingredients[indexToUpdate].ingredient.id)
    }

    @Test
    fun `onIngredientUpdate to a duplicate ingredient`() = runTest {
        // Given
        val ingredient1 = Ingredient(1, "Ingredient 1")
        val ingredient2 = Ingredient(2, "Ingredient 2")
        coEvery { ingredientsRepository.addIngredient(Ingredient(name = "Ingredient 1")) } returns ingredient1
        coEvery { ingredientsRepository.addIngredient(Ingredient(name = "Ingredient 2")) } returns ingredient2

        viewModel.onIngredientAdd("Ingredient 1")
        viewModel.onIngredientAdd("Ingredient 2")
        advanceUntilIdle()

        // When - Try to update "Ingredient 2" to "Ingredient 1" which already exists
        viewModel.onIngredientUpdate(index = 1, text = "Ingredient 1")
        advanceTimeBy(RECIPE_ERROR_MESSAGE_SHOWN_DURATION - 100)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(2, state.recipe.ingredients.size)
        assertEquals("Ingredient 1", state.recipe.ingredients[0].ingredient.name) // Unchanged
        assertEquals("Ingredient 2", state.recipe.ingredients[1].ingredient.name) // Unchanged
        assertNotNull(state.errorMessage)

        // Verify error message is cleared
        advanceUntilIdle()
        assertNull(viewModel.state.getOrAwaitValue().errorMessage)
    }

    @Test
    fun `onIngredientDelete should remove ingredient from recipe`() = runTest {
        // Given
        val ingredient = Ingredient(1, "Ingredient 1")
        coEvery { ingredientsRepository.addIngredient(any()) } returns ingredient
        viewModel.onIngredientAdd("Ingredient 1")
        advanceUntilIdle()

        // When
        viewModel.onIngredientDelete(0)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(0, state.recipe.ingredients.size)
    }

    @Test
    fun `onIngredientUpdateQuantity should update quantity of ingredient int recipe`() = runTest {
        // Given
        val ingredient = Ingredient(1, "Ingredient 1")
        coEvery { ingredientsRepository.addIngredient(any()) } returns ingredient
        viewModel.onIngredientAdd("Ingredient 1")
        advanceUntilIdle()

        // When
        viewModel.onIngredientUpdateQuantity(0, 5.0)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(5.0, state.recipe.ingredients[0].quantity, 0.0)
    }

    @Test
    fun `onIngredientUpdateUnit should update unit of ingredient int recipe`() = runTest {
        // Given
        val ingredient = Ingredient(1, "Ingredient 1")
        coEvery { ingredientsRepository.addIngredient(any()) } returns ingredient
        viewModel.onIngredientAdd("Ingredient 1")
        advanceUntilIdle()

        // When
        viewModel.onIngredientUpdateUnit(0, IngredientUnit.CUP)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(IngredientUnit.CUP, state.recipe.ingredients[0].unit)
    }

    @Test
    fun `onInstructionTextChange when text length exceeds max length should not update currentInstructionText`() =
        runTest {
            // Given
            val maxLength = "a".repeat(RECIPE_INSTRUCTIONS_MAX_LENGTH + 1)

            // When
            viewModel.onInstructionTextChange("abc")
            viewModel.onInstructionTextChange(maxLength)
            advanceUntilIdle()

            // Then
            assertEquals("abc", viewModel.state.getOrAwaitValue().currentInstructionText)
        }

    @Test
    fun `onInstructionTextChange should update currentInstructionText`() = runTest {
        // When
        viewModel.onInstructionTextChange("abc")
        advanceUntilIdle()

        // Then
        assertEquals("abc", viewModel.state.getOrAwaitValue().currentInstructionText)
    }

    @Test
    fun `onInstructionEditTextChange when text length exceeds max length should not update editInstructionText`() =
        runTest {
            // Given
            val maxLength = "a".repeat(RECIPE_INSTRUCTIONS_MAX_LENGTH + 1)

            // When
            viewModel.onInstructionEditTextChange("abc")
            viewModel.onInstructionEditTextChange(maxLength)
            advanceUntilIdle()

            // Then
            assertEquals("abc", viewModel.state.getOrAwaitValue().editInstructionText)
        }

    @Test
    fun `onInstructionEditTextChange should update editInstructionText`() = runTest {
        // When
        viewModel.onInstructionEditTextChange("abc")
        advanceUntilIdle()

        // Then
        assertEquals("abc", viewModel.state.getOrAwaitValue().editInstructionText)
    }

    @Test
    fun `onInstructionIsEditingChange when value is true should update editIngredientIndex and editIngredientText`() =
        runTest {
            // Given
            val instruction = "Step 1"
            val index = 0
            viewModel.onInstructionAdd(instruction)
            advanceUntilIdle()

            // When
            viewModel.onInstructionIsEditingChange(index, true)
            advanceUntilIdle()

            // Then
            assertEquals(index, viewModel.state.getOrAwaitValue().editInstructionIndex)
            assertEquals(instruction, viewModel.state.getOrAwaitValue().editInstructionText)
        }

    @Test
    fun `onInstructionIsEditingChange when value is false should update editIngredientIndex to null and editIngredientText to empty`() =
        runTest {
            // Given
            viewModel.onInstructionAdd("Step 1")
            advanceUntilIdle()

            // When
            viewModel.onInstructionIsEditingChange(0, false)
            advanceUntilIdle()

            // Then
            assertEquals(null, viewModel.state.getOrAwaitValue().editInstructionIndex)
            assertEquals("", viewModel.state.getOrAwaitValue().editInstructionText)
        }


    @Test
    fun `onInstructionAdd should add instruction to recipe`() = runTest {
        // When
        viewModel.onInstructionAdd("Step 1")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.instructions.size)
        assertEquals("Step 1", state.recipe.instructions[0])
        assertEquals("", state.currentInstructionText)
    }

    @Test
    fun `onInstructionUpdate should update instruction with new value`() = runTest {
        // Given
        viewModel.onInstructionAdd("Step 1")
        advanceUntilIdle()

        // When
        viewModel.onInstructionUpdate(0, "Do this")
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals("Do this", state.recipe.instructions[0])
    }

    @Test
    fun `onInstructionDelete should remove instruction from recipe`() = runTest {
        // Given
        viewModel.onInstructionAdd("Step 1")
        advanceUntilIdle()

        // When
        viewModel.onInstructionDelete(0)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertTrue(state.recipe.instructions.isEmpty())
    }

    @Test
    fun `onImagePathChange valid path should update imagePath in state`() = runTest {
        // Given
        val newPath = "/path/to/image.jpg"

        // When
        viewModel.onImagePathChange(newPath)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(newPath, state.recipe.imagePath)
    }

    @Test
    fun `onTitleChange valid title should update title in state`() = runTest {
        // Given
        val newTitle = "New Recipe Title"

        // When
        viewModel.onTitleChange(newTitle)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(newTitle, state.recipe.title)
    }

    @Test
    fun `onTitleChange title exceeding max length should not update state`() = runTest {
        // Given
        val initialTitle = "Initial Title"
        viewModel.onTitleChange(initialTitle)
        val longTitle = "a".repeat(RECIPE_TITLE_MAX_LENGTH + 1)

        // When
        viewModel.onTitleChange(longTitle)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(initialTitle, state.recipe.title)
    }

    @Test
    fun `onDescriptionChange valid description should update description in state`() = runTest {
        // Given
        val newDescription = "A new delicious description."

        // When
        viewModel.onDescriptionChange(newDescription)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(newDescription, state.recipe.description)
    }

    @Test
    fun `onDescriptionChange description exceeding max length should not update state`() = runTest {
        // Given
        val initialDescription = "Initial Description"
        viewModel.onDescriptionChange(initialDescription)
        val longDescription = "a".repeat(RECIPE_DESCRIPTION_MAX_LENGTH + 1)

        // When
        viewModel.onDescriptionChange(longDescription)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(initialDescription, state.recipe.description)
    }

    @Test
    fun `onIngredientsChange should update ingredients list in state`() = runTest {
        // Given
        val newIngredients = listOf(
            RecipeIngredient(Ingredient(name = "Flour"), 2.0, IngredientUnit.CUP),
            RecipeIngredient(Ingredient(name = "Sugar"), 1.0, IngredientUnit.CUP)
        )

        // When
        viewModel.onIngredientsChange(newIngredients)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(newIngredients, state.recipe.ingredients)
    }

    @Test
    fun `onInstructionsChange should update instructions list in state`() = runTest {
        // Given
        val newInstructions = listOf("Step 1: Mix", "Step 2: Bake")

        // When
        viewModel.onInstructionsChange(newInstructions)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(newInstructions, state.recipe.instructions)
    }

    @Test
    fun `onPrepTimeChange valid time string`() {
        // Given
        val timeString = "30"

        // When
        viewModel.onPrepTimeChange(timeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(30, state.recipe.prepTime)
    }

    @Test
    fun `onPrepTimeChange invalid time string`() {
        // Given
        val invalidTimeString = "abc"

        // When
        viewModel.onPrepTimeChange(invalidTimeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.prepTime)
    }

    @Test
    fun `onPrepTimeChange time string exceeding max length`() {
        // Given
        viewModel.onPrepTimeChange("15") // Initial value
        val longTimeString = "1".repeat(RECIPE_PREPARATION_TIME_MAX_LENGTH + 1)

        // When
        viewModel.onPrepTimeChange(longTimeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(15, state.recipe.prepTime)
    }

    @Test
    fun `onPrepTimeChange empty time string`() {
        // Given
        viewModel.onPrepTimeChange("15") // Set an initial value
        val emptyString = ""

        // When
        viewModel.onPrepTimeChange(emptyString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.prepTime)
    }

    @Test
    fun `onCookTimeChange valid time string`() {
        // Given
        val timeString = "45"

        // When
        viewModel.onCookTimeChange(timeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(45, state.recipe.cookTime)
    }

    @Test
    fun `onCookTimeChange invalid time string`() {
        // Given
        val invalidTimeString = "xyz"

        // When
        viewModel.onCookTimeChange(invalidTimeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.cookTime)
    }

    @Test
    fun `onCookTimeChange time string exceeding max length`() {
        // Given
        viewModel.onCookTimeChange("20") // Initial value
        val longTimeString = "1".repeat(RECIPE_COOKING_TIME_MAX_LENGTH + 1)

        // When
        viewModel.onCookTimeChange(longTimeString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(20, state.recipe.cookTime)
    }

    @Test
    fun `onCookTimeChange empty time string`() {
        // Given
        viewModel.onCookTimeChange("25") // Set an initial value
        val emptyString = ""

        // When
        viewModel.onCookTimeChange(emptyString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.cookTime)
    }

    @Test
    fun `onServingsChange valid servings`() {
        // Given
        val servings = 4

        // When
        viewModel.onServingsChange(servings)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(servings, state.recipe.servings)
    }

    @Test
    fun `onTagClick add new tag`() {
        // Given
        val tag = RecipeTag.BREAKFAST
        assertTrue(viewModel.state.getOrAwaitValue().recipe.tags.isEmpty())

        // When
        viewModel.onTagClick(tag)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertTrue(state.recipe.tags.contains(tag))
        assertEquals(1, state.recipe.tags.size)
    }

    @Test
    fun `onTagClick remove existing tag`() {
        // Given
        val tag = RecipeTag.DINNER
        viewModel.onTagClick(tag)
        assertTrue(viewModel.state.getOrAwaitValue().recipe.tags.contains(tag))

        // When
        viewModel.onTagClick(tag)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertFalse(state.recipe.tags.contains(tag))
        assertTrue(state.recipe.tags.isEmpty())
    }

    @Test
    fun `onHeavinessChange update heaviness`() {
        // Given
        val heaviness = RecipeHeaviness.LIGHT

        // When
        viewModel.onHeavinessChange(heaviness)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(heaviness, state.recipe.heaviness)
    }

    @Test
    fun `onCaloriesChange valid calories string`() {
        // Given
        val caloriesString = "550"

        // When
        viewModel.onCaloriesChange(caloriesString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(550, state.recipe.calories)
    }

    @Test
    fun `onCaloriesChange invalid calories string`() {
        // Given
        viewModel.onCaloriesChange("500") // Set initial value
        val invalidCaloriesString = "abc"

        // When
        viewModel.onCaloriesChange(invalidCaloriesString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.calories)
    }

    @Test
    fun `onCaloriesChange calories string exceeding max length`() {
        // Given
        viewModel.onCaloriesChange("250") // Initial value
        val longCaloriesString = "1".repeat(RECIPE_TOTAL_CALORIES_MAX_LENGTH + 1)

        // When
        viewModel.onCaloriesChange(longCaloriesString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(250, state.recipe.calories)
    }

    @Test
    fun `onCaloriesChange empty calories string`() {
        // Given
        viewModel.onCaloriesChange("300") // Set an initial value
        val emptyString = ""

        // When
        viewModel.onCaloriesChange(emptyString)

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertNull(state.recipe.calories)
    }

    @Test
    fun `onSuggestionItemSelected add new ingredient from suggestion`() = runTest {
        // Given
        val suggestedIngredient = Ingredient(name = "New Suggested Ingredient")
        val index = 0

        // When
        viewModel.onSuggestionItemSelected(index, suggestedIngredient)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.ingredients.size)
        assertEquals(suggestedIngredient, state.recipe.ingredients[0].ingredient)
    }

    @Test
    fun `onSuggestionItemSelected update existing ingredient from suggestion`() = runTest {
        // Given
        coEvery { ingredientsRepository.addIngredient(any()) } returns Ingredient(name = "Old Ingredient")
        viewModel.onIngredientAdd("Old Ingredient")
        advanceUntilIdle()

        val suggestedIngredient = Ingredient(name = "Updated Ingredient")
        val index = 0

        // When
        viewModel.onSuggestionItemSelected(index, suggestedIngredient)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals(1, state.recipe.ingredients.size)
        assertEquals(suggestedIngredient, state.recipe.ingredients[0].ingredient)
    }

    @Test
    fun `onSuggestionItemSelected state update`() = runTest {
        // Given
        val name = "Ingredient 1"
        coEvery { ingredientsRepository.addIngredient(any()) } returns Ingredient(name = name)
        viewModel.onIngredientAdd(name)
        advanceUntilIdle()
        viewModel.onIngredientIsEditingChange(0, true)
        advanceUntilIdle()
        viewModel.onIngredientTextChange("New") // Populates suggestions and currentIngredientText
        advanceUntilIdle()

        // When
        viewModel.onSuggestionItemSelected(0, Ingredient(name = "A"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.getOrAwaitValue()
        assertEquals("A", state.recipe.ingredients[0].ingredient.name)
        assertTrue(state.ingredientSuggestions.isEmpty())
        assertEquals("", state.currentIngredientText)
        assertEquals("", state.editIngredientText)
        assertNull(state.editIngredientIndex)
    }

    @Test
    fun `onDeleteSuggestedIngredient when ingredient not used and not in recipe should be deleted`() =
        runTest {
            // Given
            val ingredientToDelete = Ingredient(id = 1, name = "Unused Ingredient")
            coEvery { ingredientsRepository.isThisIngredientUsedInAnyRecipe(ingredientToDelete) } returns false
            coEvery { ingredientsRepository.deleteIngredient(ingredientToDelete) } returns Unit

            // When
            viewModel.onDeleteSuggestedIngredient(ingredientToDelete)
            advanceUntilIdle()

            // Then
            coVerify { ingredientsRepository.isThisIngredientUsedInAnyRecipe(ingredientToDelete) }
            coVerify { ingredientsRepository.deleteIngredient(ingredientToDelete) }
        }

    @Test
    fun `onDeleteSuggestedIngredient when ingredient used in other recipes should not be deleted and show error message`() =
        runTest {
            // Given
            val ingredientToDelete = Ingredient(id = 1, name = "Used Ingredient")
            val suggestions = viewModel.state.getOrAwaitValue().ingredientSuggestions
            coEvery { ingredientsRepository.isThisIngredientUsedInAnyRecipe(ingredientToDelete) } returns true
            coEvery { ingredientsRepository.deleteIngredient(ingredientToDelete) } returns Unit

            // When
            viewModel.onDeleteSuggestedIngredient(ingredientToDelete)
            advanceTimeBy(RECIPE_ERROR_MESSAGE_SHOWN_DURATION - 100)

            // Then
            val state = viewModel.state.getOrAwaitValue()
            coVerify(exactly = 0) { ingredientsRepository.deleteIngredient(ingredientToDelete) }
            assertEquals(suggestions, state.ingredientSuggestions)
            assertNotNull(state.errorMessage)

            advanceUntilIdle()
            assertNull(viewModel.state.getOrAwaitValue().errorMessage)
        }

    @Test
    fun `onDeleteSuggestedIngredient when ingredient is in current recipe should not be deleted and show error message`() =
        runTest {
            // Given
            val ingredient = Ingredient(id = 1, name = "Ingredient 1")
            val suggestions = viewModel.state.getOrAwaitValue().ingredientSuggestions
            coEvery { ingredientsRepository.addIngredient(any()) } returns ingredient
            coEvery { ingredientsRepository.isThisIngredientUsedInAnyRecipe(ingredient) } returns false
            coEvery { ingredientsRepository.deleteIngredient(ingredient) } returns Unit
            viewModel.onIngredientAdd(ingredient.name)
            advanceUntilIdle()

            // When
            viewModel.onDeleteSuggestedIngredient(ingredient)
            advanceTimeBy(RECIPE_ERROR_MESSAGE_SHOWN_DURATION - 100)

            // Then
            val state = viewModel.state.getOrAwaitValue()
            coVerify(exactly = 0) { ingredientsRepository.deleteIngredient(ingredient) }
            assertEquals(suggestions, state.ingredientSuggestions)
            assertNotNull(state.errorMessage)

            advanceUntilIdle()
            assertNull(viewModel.state.getOrAwaitValue().errorMessage)
        }
}
