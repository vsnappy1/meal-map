package com.randos.data.manager

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.manager.GroceryListManagerImpl.Companion.GROCERY_LIST
import com.randos.domain.manager.GroceryListManager
import com.randos.domain.model.GroceryIngredient
import java.time.LocalDate
import kotlinx.coroutines.CoroutineDispatcher
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
class GroceryListManagerImplTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var groceryListManager: GroceryListManager
    private lateinit var localDate: LocalDate
    private lateinit var dispatcher: CoroutineDispatcher

    @Before
    fun setUp() {
        val context = getApplicationContext<Application>()
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        dispatcher = StandardTestDispatcher()
        localDate = LocalDate.of(2025, 10, 11) // Saturday
        groceryListManager = GroceryListManagerImpl(sharedPreferences, dispatcher, localDate)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun markIngredientAsChecked_when_ingredient_is_not_in_list_should_add_it() = runTest(dispatcher) {
        // Given
        val ingredient =
            GroceryIngredient(name = "Apple", isChecked = false, amountsByUnit = listOf())
        val week = 1

        // When
        groceryListManager.markIngredientAsChecked(ingredient, week)

        // Then
        val savedIngredients = groceryListManager.getCheckedGroceryIngredientsNameForWeek(week)
        assertTrue(savedIngredients.contains(ingredient.name))
    }

    @Test
    fun markIngredientAsUnchecked_when_ingredient_is_in_list_should_remove_it() = runTest(dispatcher) {
        // Given
        val ingredient =
            GroceryIngredient(name = "Apple", isChecked = true, amountsByUnit = listOf())
        val week = 1
        groceryListManager.markIngredientAsChecked(ingredient, week)
        val result1 = groceryListManager.getCheckedGroceryIngredientsNameForWeek(week)
        assertTrue(result1.contains(ingredient.name))

        // When
        groceryListManager.markIngredientAsUnchecked(ingredient, week)

        // Then
        val result2 = groceryListManager.getCheckedGroceryIngredientsNameForWeek(week)
        assertFalse(result2.contains(ingredient.name))
    }

    @Test
    fun getCheckedGroceryIngredientsForWeek_should_return_list_of_checked_ingredients() = runTest(dispatcher) {
        // Given
        val ingredient1 = GroceryIngredient(false, "Apple", listOf())
        val ingredient2 = GroceryIngredient(false, "Potato", listOf())
        val ingredient3 = GroceryIngredient(false, "Onion", listOf())
        val ingredient4 = GroceryIngredient(false, "Wheat Flour", listOf())
        val week = 1

        // Mark ingredients as checked
        groceryListManager.markIngredientAsChecked(ingredient1, week)
        groceryListManager.markIngredientAsChecked(ingredient2, week)
        groceryListManager.markIngredientAsChecked(ingredient3, week)

        // Mark ingredients as checked in next week
        groceryListManager.markIngredientAsChecked(ingredient4, week + 1)

        // Mark ingredients as unchecked
        groceryListManager.markIngredientAsUnchecked(ingredient2, week)

        // When
        val result = groceryListManager.getCheckedGroceryIngredientsNameForWeek(week)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains(ingredient1.name))
        assertTrue(result.contains(ingredient3.name))
        assertFalse(result.contains(ingredient2.name))
    }

    @Test
    fun getCheckedGroceryIngredientsForWeek_should_return_empty_list_when_no_ingredients_are_checked() = runTest(dispatcher) {
        // When
        val result = groceryListManager.getCheckedGroceryIngredientsNameForWeek(1)

        // Then
        assertEquals(0, result.size)
    }

    @Test
    fun findMiddlePointOfWeek_should_return_wednesday_of_the_week() = runTest(dispatcher) {
        // Given
        val ingredient = GroceryIngredient(false, "Apple", listOf())

        listOf<Long>(0, 1, 2).forEach { day ->
            // Saturday, Sunday, Monday
            val date = localDate.plusDays(day)
            val week = 1

            // When
            val groceryListManager = GroceryListManagerImpl(sharedPreferences, dispatcher, date)
            groceryListManager.markIngredientAsChecked(ingredient, week)

            // Then
            val key = "${GROCERY_LIST}_2025-10-15"
            assertEquals(1, sharedPreferences.all.keys.size)
            assertEquals(key, sharedPreferences.all.keys.first())
        }
    }
}
