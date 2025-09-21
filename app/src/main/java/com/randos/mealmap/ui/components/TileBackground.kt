package com.randos.mealmap.ui.components

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.randos.mealmap.utils.Utils


@Composable
fun TileBackground(
    modifier: Modifier = Modifier,
    columns: Int = Utils.foodIcons.size,
    rows: Int = 5
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(columns)
    ) {
        for (i in 0 until rows) {
            items(columns) { index ->
                Icon(
                    imageVector = Utils.foodIcons[(i + index) % columns],
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun TileBackgroundPreview() {
    TileBackground(
        columns = 7,
        rows = 3
    )
}