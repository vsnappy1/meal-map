package com.randos.domain.model

import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import java.time.LocalDate

data class Recipe(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val imagePath: String? = null,
    val instructions: List<String> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList(),
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val tags: List<RecipeTag> = emptyList(),
    val calories: Int? = null,
    val heaviness: RecipeHeaviness? = null,
    val dateCreated: LocalDate
)
