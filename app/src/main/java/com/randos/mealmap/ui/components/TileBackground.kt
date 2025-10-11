package com.randos.mealmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.randos.mealmap.utils.Constants

@Composable
fun TileBackground(
    modifier: Modifier = Modifier,
    columns: Int = Constants.foodIcons.size,
    rows: Int = 5,
    iconSize: Dp = 32.dp,
    spaceBetweenRow: Dp = 0.dp
) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(rows) { rowIndex ->
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(columns) { colIndex ->
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = Constants.foodIcons[(rowIndex + colIndex) % Constants.foodIcons.size],
                        contentDescription = null,
                    )
                }
            }
            if (rowIndex < rows - 1) {
                Spacer(modifier = Modifier.height(spaceBetweenRow))
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