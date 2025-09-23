package com.randos.mealmap.ui.recipe_details

import android.content.Context
import android.content.Intent
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

@Composable
fun RecipeDetailsScreen(
    id: Long,
    onEdit: (id: Long) -> Unit,
    onDelete: () -> Unit,
    viewModel: RecipeDetailsScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.observeAsState()
    RecipeDetailsScreen(
        recipe = state.value?.recipe,
        onEdit = { onEdit(id) },
        onDelete = { viewModel.deleteRecipe { onDelete() } })
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
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
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
                    onClick = { isDeleteDialogOpen = true })
                ActionButton(imageVector = Icons.Rounded.Edit, onClick = onEdit)
                ActionButton(imageVector = Icons.Rounded.Share, onClick = {
                    shareRecipe(context = context, recipe = recipe)
                })
            }
        }
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
                    text = "${formatQuantity(it.quantity)} ${it.unit?.displayName ?: "unit"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        SubHeading(text = "Instructions")
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
        SubHeading(text = "Extra Details")
        RecipeExtraDetails(recipe = recipe)

        if (isDeleteDialogOpen) {
            DeleteConfirmationDialog(
                onConfirm = {
                    onDelete()
                    isDeleteDialogOpen = false
                },
                onDismiss = { isDeleteDialogOpen = false }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
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
                value1 = recipe.tag?.value ?: "--",
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

fun recipeToShareableText(recipe: Recipe): String {
    val builder = StringBuilder()

    // Title
    builder.appendLine("🍽 ${recipe.title}")
    builder.appendLine()

    // Description (if present)
    recipe.description?.let {
        if (it.isNotBlank()) {
            builder.appendLine(it.trim())
            builder.appendLine()
        }
    }

    // Metadata
    recipe.prepTime?.let { builder.appendLine("⏱ Prep Time: ${formatTime(it)}") }
    recipe.cookTime?.let { builder.appendLine("🍳 Cook Time: ${formatTime(it)}") }
    recipe.servings?.let { builder.appendLine("👥 Servings: $it") }
    recipe.tag?.let { builder.appendLine("🏷 Tag: $it") }
    recipe.heaviness?.let { builder.appendLine("⚖️ Heaviness: $it") }
    recipe.calories?.let { builder.appendLine("🔥 Calories: $it kcal") }
    builder.appendLine()

    // Ingredients
    builder.appendLine("🛒 Ingredients:")
    recipe.ingredients.forEach { ri ->
        builder.appendLine("- ${formatQuantity(ri.quantity)} ${ri.unit?.displayName ?: "unit"} ${ri.ingredient.name}")
    }
    builder.appendLine()

    // Instructions
    builder.appendLine("👩‍🍳 Instructions:")
    recipe.instructions.forEachIndexed { index, step ->
        builder.appendLine("${index + 1}. $step")
    }

    return builder.toString().trim()
}

fun shareRecipe(context: Context, recipe: Recipe) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, recipeToShareableText(recipe))
        putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe!")
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share via")
    context.startActivity(shareIntent)
}

fun formatTime(minutes: Int?): String {
    if (minutes == null) return "--"
    if (minutes <= 0) return "0 min"

    val hours = minutes / 60
    val mins = minutes % 60

    return buildString {
        if (hours > 0) {
            append(hours)
            append(" hr")
            if (hours > 1) append("s") // plural
        }
        if (hours > 0 && mins > 0) append(" ")
        if (mins > 0) {
            append(mins)
            append(" min")
            if (mins > 1) append("s") // plural
        }
    }
}

private fun formatQuantity(quantity: Double): String {
    // If it's basically an integer, show as integer
    if (quantity % 1.0 == 0.0) {
        return quantity.toInt().toString()
    }

    // Map common fractions
    val fractions = mapOf(
        0.25 to "¼",
        0.33 to "⅓",
        0.5 to "½",
        0.66 to "⅔",
        0.75 to "¾"
    )

    // Find closest match among fractions
    val roundedFraction = fractions.minByOrNull { (value, _) ->
        kotlin.math.abs(quantity % 1 - value)
    }

    if (roundedFraction != null && kotlin.math.abs(quantity % 1 - roundedFraction.key) < 0.05) {
        val whole = quantity.toInt()
        return if (whole > 0) {
            "$whole${roundedFraction.value}"
        } else {
            roundedFraction.value
        }
    }

    // Fallback: show up to 2 decimal places
    return String.format("%.2f", quantity).trimEnd('0').trimEnd('.')
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