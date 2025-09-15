package com.randos.mealmap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.randos.mealmap.ui.account.AccountScreen
import com.randos.mealmap.ui.grocery_list.GroceryListScreen
import com.randos.mealmap.ui.home.HomeScreen
import com.randos.mealmap.ui.recipe_add.AddRecipeScreen
import com.randos.mealmap.ui.recipe_details.RecipeDetailsScreen
import com.randos.mealmap.ui.recipes.RecipesScreen
import com.randos.mealmap.ui.settings.SettingsScreen
import com.randos.mealmap.ui.navigation.Destination.*

@Composable
fun MealMapNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen()
        }

        composable<Recipes> {
            RecipesScreen(
                onAddNewRecipe = {
                    navController.navigate(AddRecipe)
                },
                onRecipeClick = { id ->
                    navController.navigate(RecipeDetails(id))
                })
        }

        composable<Grocery> {
            GroceryListScreen()
        }

        composable<Settings> {
            SettingsScreen()
        }

        composable<Account> {
            AccountScreen()
        }

        composable<RecipeDetails> {
            val recipeDetails = it.toRoute<RecipeDetails>()
            RecipeDetailsScreen(
                id = recipeDetails.id,
                onEdit = { id ->
                    navController.navigate(ModifyRecipe(id))
                }
            )
        }

        composable<ModifyRecipe> {
            val modifyRecipe = it.toRoute<ModifyRecipe>()
            AddRecipeScreen(id = modifyRecipe.id)
        }

        composable<AddRecipe>() {
            AddRecipeScreen()
        }
    }
}
