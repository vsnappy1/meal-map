package com.randos.mealmap.utils

import java.util.Locale

object NumberUtils {
    fun formatIngredientQuantity(quantity: Double): String {
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
}