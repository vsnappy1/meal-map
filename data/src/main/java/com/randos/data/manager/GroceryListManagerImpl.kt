package com.randos.data.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.randos.domain.manager.GroceryListManager
import com.randos.domain.model.GroceryIngredient
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class GroceryListManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: CoroutineDispatcher,
    private val localDate: LocalDate
) : GroceryListManager {

    companion object {
        const val GROCERY_LIST = "grocery-list"
    }

    private val gson = Gson()

    override suspend fun markIngredientAsChecked(ingredient: GroceryIngredient, week: Int) = withContext(dispatcher) {
        markIngredient(ingredient.name, week, true)
    }

    override suspend fun markIngredientAsUnchecked(ingredient: GroceryIngredient, week: Int) = withContext(dispatcher) {
        markIngredient(ingredient.name, week, false)
    }

    override suspend fun getCheckedGroceryIngredientsNameForWeek(week: Int): HashSet<String> = withContext(dispatcher) {
        val wednesday = findMiddlePointOfWeek(week)
        val key = "${GROCERY_LIST}_$wednesday"
        val json = sharedPreferences.getString(key, null)
        return@withContext if (json != null) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            gson.fromJson<MutableSet<String>>(json, type)
        } else {
            setOf()
        }.toHashSet()
    }

    // TODO implement cleaning up logic for grocery list
    private fun markIngredient(ingredientName: String, weekOffset: Int, checked: Boolean) {
        val wednesday = findMiddlePointOfWeek(weekOffset)
        val key = "${GROCERY_LIST}_$wednesday"
        val json = sharedPreferences.getString(key, null)
        val checkedIngredients = if (json != null) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            gson.fromJson<MutableSet<String>>(json, type)
        } else {
            mutableSetOf()
        }

        val ingredient = ingredientName
        if (checked) {
            checkedIngredients.add(ingredient)
        } else {
            checkedIngredients.remove(ingredient)
        }

        val updatedJson = gson.toJson(checkedIngredients)
        sharedPreferences.edit {
            putString(key, updatedJson)
            apply()
        }
    }

    // Find LocalDate for Wednesday of the week
    private fun findMiddlePointOfWeek(weekOffset: Int): LocalDate {
        val dateInWeek = localDate.plusWeeks(weekOffset.toLong())
        return dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY))
    }
}
