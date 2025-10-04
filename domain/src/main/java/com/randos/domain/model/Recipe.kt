package com.randos.domain.model

import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import java.time.LocalDate

data class Recipe(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val imagePath: String?,
    val instructions: List<String>,
    val ingredients: List<RecipeIngredient>,
    val prepTime: Int?,
    val cookTime: Int?,
    val servings: Int?,
    val tags: List<RecipeTag>,
    val calories: Int?,
    val heaviness: RecipeHeaviness?,
    val dateCreated: LocalDate,
)
