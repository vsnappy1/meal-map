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

internal class GroceryListManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : GroceryListManager {

    companion object {
        private const val GROCERY_LIST = "grocery_list"
    }

    private val gson = Gson()

    override suspend fun markIngredientAsChecked(
        ingredient: GroceryIngredient,
        week: Int
    ) {
        markIngredient(ingredient, week, true)
    }

    override suspend fun markIngredientAsUnchecked(
        ingredient: GroceryIngredient,
        week: Int
    ) {
        markIngredient(ingredient, week, false)
    }

    override suspend fun getCheckedGroceryIngredientsForWeek(week: Int): Set<GroceryIngredient> {
        val wednesday = findMiddlePointOfWeek(week)
        val key = "${GROCERY_LIST}_$wednesday"
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableSet<GroceryIngredient>>() {}.type
            gson.fromJson<MutableSet<GroceryIngredient>>(json, type)
        } else {
            mutableSetOf()
        }
    }

    // TODO implement cleaning up logic for grocery list
    private fun markIngredient(groceryIngredient: GroceryIngredient, week: Int, checked: Boolean) {
        val wednesday = findMiddlePointOfWeek(week)
        val key = "${GROCERY_LIST}_$wednesday"
        val json = sharedPreferences.getString(key, null)
        val checkedIngredients = if (json != null) {
            val type = object : TypeToken<MutableSet<GroceryIngredient>>() {}.type
            gson.fromJson<MutableSet<GroceryIngredient>>(json, type)
        } else {
            mutableSetOf()
        }

        val ingredient = groceryIngredient.copy(isChecked = false)
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
        val dateInWeek = LocalDate.now().plusWeeks(weekOffset.toLong())
        return dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
    }
}