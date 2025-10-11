package com.randos.mealmap.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Recipe
import com.randos.mealmap.utils.Constants

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
            RecipeItemImage(
                modifier = Modifier
                    .size(75.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .clip(shape = MaterialTheme.shapes.small)
                , imagePath = recipe.imagePath
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = recipe.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RecipeItemPreview() {
    RecipeItem(
        recipe = Constants.recipe,
        onClick = {}
    )
}
