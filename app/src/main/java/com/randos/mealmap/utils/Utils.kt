package com.randos.mealmap.utils

import com.randos.domain.model.Recipe
import com.randos.mealmap.utils.CalendarUtils.formatTime
import com.randos.mealmap.utils.NumberUtils.formatIngredientQuantity

object Utils {
    fun recipeToShareableText(recipe: Recipe): String {
        val builder = StringBuilder()

        // Title
        builder.appendLine("🍽 ${recipe.title}")
        builder.appendLine()

        // Description (if present)
        recipe.description?.let {
            if (it.isNotBlank()) {
                builder.appendLine(it.trim())
                builder.appendLine()
            }
        }

        // Metadata
        recipe.prepTime?.let { builder.appendLine("⏱ Prep Time: ${formatTime(it)}") }
        recipe.cookTime?.let { builder.appendLine("🍳 Cook Time: ${formatTime(it)}") }
        recipe.servings?.let { builder.appendLine("👥 Servings: $it") }
        recipe.tags.let { builder.appendLine("🏷 Tag: ${it.joinToString(", ") { tag -> tag.value }}") }
        recipe.heaviness?.let { builder.appendLine("⚖️ Heaviness: $it") }
        recipe.calories?.let { builder.appendLine("🔥 Calories: $it kcal") }
        builder.appendLine()

        // Ingredients
        builder.appendLine("🛒 Ingredients:")
        recipe.ingredients.forEach { ri ->
            builder.appendLine("- ${formatIngredientQuantity(ri.quantity)} ${ri.unit?.value ?: "unit"} ${ri.ingredient.name}")
        }
        builder.appendLine()

        // Instructions
        builder.appendLine("👩‍🍳 Instructions:")
        recipe.instructions.forEachIndexed { index, step ->
            builder.appendLine("${index + 1}. $step")
        }

        return builder.toString().trim()
    }
}