package com.randos.domain.manager

import com.randos.domain.model.GroceryIngredient

interface GroceryListManager {
    suspend fun markIngredientAsChecked(ingredient: GroceryIngredient, week: Int)

    suspend fun markIngredientAsUnchecked(ingredient: GroceryIngredient, week: Int)

    suspend fun getCheckedGroceryIngredientsNameForWeek(week: Int): Set<String>
}
