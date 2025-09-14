package com.randos.mealmap.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.savedstate.SavedState
import com.randos.mealmap.ui.navigation.Destination.Account
import com.randos.mealmap.ui.navigation.Destination.Grocery
import com.randos.mealmap.ui.navigation.Destination.Home
import com.randos.mealmap.ui.navigation.Destination.Recipes
import com.randos.mealmap.ui.navigation.Destination.Settings

/**
 * A listener that observes changes in the navigation destination and updates the visibility of the bottom navigation bar.
 */
class MealMapDestinationChangedListener(
    private val updateBottomSheetVisibility: (Boolean) -> Unit
) :
    NavController.OnDestinationChangedListener {
    /**
     * Routes that should show the bottom navigation bar.
     */
    val routes = listOf(
        Grocery::class.simpleName,
        Recipes::class.simpleName,
        Home::class.simpleName,
        Account::class.simpleName,
        Settings::class.simpleName
    )

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: SavedState?
    ) {
        for (route in routes) {
            if (destination.route?.contains(route.orEmpty()) == true) {
                updateBottomSheetVisibility(true)
                return
            }
        }
        updateBottomSheetVisibility(false)
    }
}