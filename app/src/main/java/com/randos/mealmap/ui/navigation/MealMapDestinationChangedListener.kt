package com.randos.mealmap.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.savedstate.SavedState

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
        Destination.Grocery::class.simpleName,
        Destination.Recipes::class.simpleName,
        Destination.Home::class.simpleName,
        Destination.Account::class.simpleName,
        Destination.Settings::class.simpleName
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