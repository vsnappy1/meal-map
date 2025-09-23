package com.randos.mealmap.ui.recipes

import com.randos.domain.model.Recipe
import com.randos.domain.type.RecipesFilter
import com.randos.domain.type.RecipesSort
import com.randos.domain.type.SortOrder

data class RecipesScreenState (
    val searchText: String = "",
    val recipes: List<Recipe> = emptyList(),
    val filter: Any? = null,
    val sort: RecipesSort? = null,
    val sortOrder: SortOrder = SortOrder.ASCENDING
)
