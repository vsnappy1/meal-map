package com.randos.domain.model

import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import java.util.Date

data class Recipe(
    val id: Long,
    val title: String,
    val description: String,
    val imagePath: String?,
    val instructions: List<String>,
    val ingredients: List<RecipeIngredient>,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val tag: RecipeTag,
    val calories: Int,
    val heaviness: RecipeHeaviness,
    val dateCreated: Date,
)
