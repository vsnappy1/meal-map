package com.randos.mealmap.ui.recipe_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.randos.domain.model.Recipe
import com.randos.mealmap.ui.components.TileBackground
import com.randos.mealmap.ui.theme.iconButtonColors
import com.randos.mealmap.utils.Utils
import com.randos.mealmap.utils.Utils.formatQuantity
import com.randos.mealmap.utils.Utils.formatTime
import com.randos.mealmap.utils.Utils.shareRecipe

@Composable
fun RecipeDetailsScreen(
    id: Long,
    onEdit: (id: Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: RecipeDetailsScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.observeAsState()
    RecipeDetailsScreen(
        recipe = state.value?.recipe,
        onEdit = { onEdit(id) },
        onDelete = { viewModel.deleteRecipe { onDeleted() } })
    LaunchedEffect(Unit) {
        viewModel.getRecipeDetails(id)
    }
}

@Composable
private fun RecipeDetailsScreen(
    recipe: Recipe?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    if (recipe == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderImageWithActionButtons(
            recipe = recipe,
            onEdit = onEdit,
            onDelete = onDelete
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = recipe.title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        recipe.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        SubHeading(text = "Ingredients")
        RecipeIngredients(recipe)
        SubHeading(text = "Instructions")
        RecipeInstructions(recipe)
        SubHeading(text = "Extra Details")
        RecipeExtraDetails(recipe = recipe)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RecipeInstructions(recipe: Recipe) {
    recipe.instructions.forEachIndexed { index, instruction ->
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step ${index + 1}:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = instruction,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Composable
private fun RecipeIngredients(recipe: Recipe) {
    recipe.ingredients.forEach {
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = it.ingredient.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W400
            )
            Text(
                text = "${formatQuantity(it.quantity)} ${it.unit?.value ?: "unit"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HeaderImageWithActionButtons(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current
    var isDeleteConfirmationDialogOpen by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(250.dp)
            .clip(shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (recipe.imagePath != null) {
            AsyncImage(
                model = recipe.imagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Card {
                TileBackground(
                    modifier = Modifier.padding(vertical = 4.dp),
                    rows = 6,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                    shape = CircleShape
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ActionButton(
                imageVector = Icons.Rounded.Delete,
                onClick = { isDeleteConfirmationDialogOpen = true })
            ActionButton(imageVector = Icons.Rounded.Edit, onClick = onEdit)
            ActionButton(imageVector = Icons.Rounded.Share, onClick = {
                shareRecipe(context = context, recipe = recipe)
            })
        }
    }
    if (isDeleteConfirmationDialogOpen) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete()
                isDeleteConfirmationDialogOpen = false
            },
            onDismiss = { isDeleteConfirmationDialogOpen = false }
        )
    }
}

@Composable
private fun SubHeading(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier.padding(top = 8.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
    HorizontalDivider()
}

@Composable
private fun RecipeExtraDetails(modifier: Modifier = Modifier, recipe: Recipe) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            RecipeExtraRow(
                title1 = "Prep Time",
                value1 = formatTime(recipe.prepTime),
                title2 = "Cook Time",
                value2 = formatTime(recipe.cookTime),
                title3 = "Servings",
                value3 = recipe.servings?.toString() ?: "--"
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecipeExtraRow(
                title1 = "Tag",
                value1 = if (recipe.tags.isEmpty()) "--" else recipe.tags.joinToString(", ") { it.value },
                title2 = "Heaviness",
                value2 = recipe.heaviness?.value ?: "--",
                title3 = "Total Calories",
                value3 = recipe.calories?.toString() ?: "--"
            )
        }
    }
}

@Composable
private fun RecipeExtraRow(
    title1: String,
    value1: String,
    title2: String,
    value2: String,
    title3: String,
    value3: String
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        RecipeExtraItem(
            modifier = Modifier.align(Alignment.CenterStart),
            title = title1,
            value = value1
        )
        RecipeExtraItem(
            modifier = Modifier.align(Alignment.Center),
            title = title2,
            value = value2
        )
        RecipeExtraItem(
            modifier = Modifier.align(Alignment.CenterEnd),
            title = title3,
            value = value3
        )
    }
}

@Composable
private fun RecipeExtraItem(modifier: Modifier = Modifier, title: String, value: String) {
    Column(modifier = modifier.width(100.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Recipe")
        },
        text = {
            Text("Are you sure you want to delete this recipe? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = iconButtonColors()
    ) {
        Icon(
            modifier = Modifier.padding(6.dp),
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RecipeDetailsScreenPreview() {
    MaterialTheme {
        RecipeDetailsScreen(Utils.recipe, onEdit = {}, onDelete = {})
    }
}
