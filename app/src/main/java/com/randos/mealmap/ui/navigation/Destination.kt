package com.randos.mealmap.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    object Home : Destination()

    @Serializable
    object Recipes : Destination()

    @Serializable
    object Grocery : Destination()

    @Serializable
    object Settings : Destination()

    @Serializable
    object Account : Destination()

    @Serializable
    data class RecipeDetails(val id: Long) : Destination()

    @Serializable
    data class ModifyRecipe(val id: Long) : Destination()

    @Serializable
    object AddRecipe : Destination()
}