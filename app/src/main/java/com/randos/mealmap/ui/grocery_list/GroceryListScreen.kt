package com.randos.mealmap.ui.grocery_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.randos.mealmap.ui.components.ScreenHeadingText

@Composable
fun GroceryListScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ScreenHeadingText(text = "Grocery List")
    }
}