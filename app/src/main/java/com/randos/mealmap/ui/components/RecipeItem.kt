package com.randos.mealmap.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.rememberAsyncImagePainter
import com.randos.domain.model.Recipe
import com.randos.mealmap.R
import com.randos.mealmap.utils.Utils

@Composable
fun RecipeItem(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: (Long) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable { onClick(recipe.id) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            val painter: Painter = if (!recipe.imagePath.isNullOrEmpty()) {
                rememberAsyncImagePainter(model = recipe.imagePath?.toUri())
            } else {
                painterResource(id = R.drawable.round_soup_kitchen_24)
            }
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.recipe_image),
                modifier = Modifier
                    .size(75.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = recipe.description.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RecipeItemPreview() {
    RecipeItem(
        recipe = Utils.recipe,
        onClick = {}
    )
}
