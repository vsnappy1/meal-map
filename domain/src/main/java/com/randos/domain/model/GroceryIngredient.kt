package com.randos.domain.model

import com.randos.domain.type.IngredientUnit

data class GroceryIngredient(
    val isChecked: Boolean = false,
    val name: String,
    val amountsByUnit: List<Pair<IngredientUnit?, Double>>
)
