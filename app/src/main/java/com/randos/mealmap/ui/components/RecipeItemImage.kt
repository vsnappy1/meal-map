package com.randos.mealmap.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.randos.mealmap.R

@Composable
fun RecipeItemImage(modifier: Modifier = Modifier, imagePath: String?) {
    Box(modifier = modifier) {
        if (imagePath.isNullOrEmpty()) {
            Image(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                painter = painterResource(id = R.drawable.round_soup_kitchen_24),
                contentDescription = stringResource(R.string.recipe_image),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imagePath,
                contentDescription = stringResource(R.string.recipe_image),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
private fun RecipeItemImagePreview() {
    RecipeItemImage(imagePath = null)
}
