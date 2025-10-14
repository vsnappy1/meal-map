package com.randos.mealmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.randos.mealmap.ui.navigation.MealMapBottomNavigationBar
import com.randos.mealmap.ui.navigation.MealMapDestinationChangedListener
import com.randos.mealmap.ui.navigation.MealMapNavHost
import com.randos.mealmap.ui.theme.MealMapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var isBottomNavigationBarVisible by remember { mutableStateOf(true) }
            val destinationChangedListener = MealMapDestinationChangedListener { isVisible ->
                isBottomNavigationBarVisible = isVisible
            }
            MealMapTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = isBottomNavigationBarVisible,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                        ) {
                            MealMapBottomNavigationBar(
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MealMapNavHost(navController)
                    }
                }
            }
            /*
             * The destinationChangedListener's lifecycle is tied to the composition using a
             * [DisposableEffect] to ensure it is properly added and removed.
             */
            DisposableEffect(Unit) {
                navController.addOnDestinationChangedListener(destinationChangedListener)
                onDispose {
                    navController.removeOnDestinationChangedListener(destinationChangedListener)
                }
            }
        }
    }
}
