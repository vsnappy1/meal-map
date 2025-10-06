package com.randos.mealmap.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    data class Home(val name: String? = Home::class.simpleName) : Destination()

    @Serializable
    data class Recipes(val name: String? = Recipes::class.simpleName) : Destination()

    @Serializable
    data class Grocery(val name: String? = Grocery::class.simpleName) : Destination()

    @Serializable
    data class Settings(val name: String? = Settings::class.simpleName) : Destination()

    @Serializable
    data class Account(val name: String? = Account::class.simpleName) : Destination()

    @Serializable
    data class RecipeDetails(val id: Long) : Destination()

    @Serializable
    data class ModifyRecipe(val id: Long) : Destination()

    @Serializable
    object AddRecipe : Destination()
}