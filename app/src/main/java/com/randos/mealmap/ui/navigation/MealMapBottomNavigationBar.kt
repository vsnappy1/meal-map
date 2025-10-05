package com.randos.mealmap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SoupKitchen
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.randos.mealmap.ui.navigation.Destination.Account
import com.randos.mealmap.ui.navigation.Destination.Grocery
import com.randos.mealmap.ui.navigation.Destination.Home
import com.randos.mealmap.ui.navigation.Destination.Recipes
import com.randos.mealmap.ui.navigation.Destination.Settings

private data class NavigationItem<T>(
    val title: String,
    val icon: ImageVector,
    val route: T
)

private val navigationItems = listOf(
    NavigationItem("Grocery", Icons.Rounded.ShoppingCart, Grocery),
    NavigationItem("Recipes", Icons.Rounded.SoupKitchen, Recipes),
    NavigationItem("Home", Icons.Rounded.Home, Home),
    NavigationItem("Account", Icons.Rounded.Person, Account),
    NavigationItem("Settings", Icons.Rounded.Settings, Settings),
)

@Composable
fun MealMapBottomNavigationBar(modifier: Modifier = Modifier, navController: NavController) {
    val destinationSaver = DestinationSaver()
    var selectedDestination: Destination by rememberSaveable(stateSaver = destinationSaver) {
        val currentDestinationRoute = navController.currentDestination?.route.orEmpty()
        mutableStateOf(destinationSaver.restore(currentDestinationRoute) ?: Home)
    }
    NavigationBar(modifier = modifier) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = selectedDestination == item.route,
                onClick = {
                    selectedDestination = item.route
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedDestination = destinationSaver.restore(destination.route.orEmpty()) ?: Home
    }
}

private class DestinationSaver : Saver<Destination, String> {
    override fun SaverScope.save(value: Destination): String? {
        return value::class.qualifiedName
    }

    override fun restore(value: String): Destination? {
        return when (value) {
            Grocery::class.qualifiedName -> return Grocery
            Recipes::class.qualifiedName -> return Recipes
            Home::class.qualifiedName -> return Home
            Account::class.qualifiedName -> return Account
            Settings::class.qualifiedName -> return Settings
            else -> null
        }
    }
}