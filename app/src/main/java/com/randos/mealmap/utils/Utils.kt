package com.randos.mealmap.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Icecream
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.Liquor
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.RamenDining
import androidx.core.content.FileProvider
import com.randos.domain.model.Ingredient
import com.randos.domain.model.Recipe
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

object Utils {
    val recipe = Recipe(
        id = 1,
        title = "Recipe Title",
        description = "Recipe Description",
        imagePath = null,
        ingredients = listOf(
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 1"),
                quantity = 1.0,
                unit = IngredientUnit.GRAM
            ),
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 2"),
                quantity = 2.5,
                unit = IngredientUnit.CUP
            ),
            RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient 3"),
                quantity = 0.25,
                unit = IngredientUnit.PIECE
            )
        ),
        instructions = listOf("Instruction 1", "Instruction 2", "Instruction 3"),
        prepTime = 10,
        cookTime = 400,
        servings = 2,
        tags = listOf(RecipeTag.CHICKEN),
        calories = 100,
        heaviness = RecipeHeaviness.MEDIUM,
        dateCreated = LocalDate.now()
    )
    val recipes = listOf(recipe)

    val servings = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

    val recipeSort: List<RecipesSort> = RecipesSort.entries.toList()

    val ingredientUnits = IngredientUnit.entries.toList()

    val recipeHeaviness = RecipeHeaviness.entries.toList()

    val recipeTags: List<RecipeTag> = RecipeTag.entries.toList()

    val foodIcons = listOf(
        Icons.Rounded.Fastfood,
        Icons.Rounded.LocalPizza,
        Icons.Rounded.RamenDining,
        Icons.Rounded.KebabDining,
        Icons.Rounded.Icecream,
        Icons.Rounded.DinnerDining,
        Icons.Rounded.Liquor
    )

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
            builder.appendLine("- ${formatQuantity(ri.quantity)} ${ri.unit?.value ?: "unit"} ${ri.ingredient.name}")
        }
        builder.appendLine()

        // Instructions
        builder.appendLine("👩‍🍳 Instructions:")
        recipe.instructions.forEachIndexed { index, step ->
            builder.appendLine("${index + 1}. $step")
        }

        return builder.toString().trim()
    }

    fun shareRecipe(context: Context, recipe: Recipe) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, recipeToShareableText(recipe))
            putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe!")
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share via")
        context.startActivity(shareIntent)
    }

    fun formatTime(minutes: Int?): String {
        if (minutes == null) return "--"
        if (minutes <= 0) return "0 min"

        val hours = minutes / 60
        val mins = minutes % 60

        return buildString {
            if (hours > 0) {
                append(hours)
                append(" hr")
                if (hours > 1) append("s") // plural
            }
            if (hours > 0 && mins > 0) append(" ")
            if (mins > 0) {
                append(mins)
                append(" min")
                if (mins > 1) append("s") // plural
            }
        }
    }

    fun formatQuantity(quantity: Double): String {
        // If it's basically an integer, show as integer
        if (quantity % 1.0 == 0.0) {
            return quantity.toInt().toString()
        }

        // Map common fractions
        val fractions = mapOf(
            0.25 to "¼",
            0.33 to "⅓",
            0.5 to "½",
            0.66 to "⅔",
            0.75 to "¾"
        )

        // Find closest match among fractions
        val roundedFraction = fractions.minByOrNull { (value, _) ->
            kotlin.math.abs(quantity % 1 - value)
        }

        if (roundedFraction != null && kotlin.math.abs(quantity % 1 - roundedFraction.key) < 0.05) {
            val whole = quantity.toInt()
            return if (whole > 0) {
                "$whole${roundedFraction.value}"
            } else {
                roundedFraction.value
            }
        }

        // Fallback: show up to 2 decimal places
        return String.format(Locale.getDefault(), "%.2f", quantity).trimEnd('0').trimEnd('.')
    }

    suspend fun copyUriToAppStorage(context: Context, uri: Uri): Uri? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "meal_map_${System.currentTimeMillis()}.jpg"
                )
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    fun getCurrentWeek(): Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
}