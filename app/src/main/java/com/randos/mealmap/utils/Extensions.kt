package com.randos.mealmap.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.defaultMainContainerPadding(): Modifier {
    return this.padding(start = 16.dp, end = 16.dp, top = 16.dp)
}
