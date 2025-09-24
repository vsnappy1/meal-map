package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Ingredient
import com.randos.mealmap.ui.theme.iconButtonColors

@Composable
fun RecipeAddTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDoneClick: (String) -> Unit,
    hintText: String = "",
    isEditing: Boolean = false,
    suggestions: List<Ingredient> = emptyList(),
    onSuggestionItemSelected: (Ingredient) -> Unit = {},
    onDeleteSuggestion: (Ingredient) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = if (isEditing) 8.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                singleLine = true,
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (value.isNotEmpty()) {
                        onDoneClick(value)
                    }
                    focusManager.clearFocus()
                }),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)

            )
            Text(
                modifier = Modifier
                    .alpha(if (value.isEmpty()) 1f else 0f)
                    .fillMaxWidth(),
                text = hintText,
                color = MaterialTheme.colorScheme.outline
            )
        }

        IconButton(
            modifier = Modifier
                .size(24.dp),
            onClick = {
                onDoneClick(value)
                focusManager.clearFocus()
            },
            enabled = value.isNotEmpty(),
            colors = iconButtonColors(value.isNotEmpty())
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
            )
        }
    }

    AnimatedVisibility(!isEditing || suggestions.isNotEmpty()) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
    }

    AnimatedContent(
        modifier = Modifier.fillMaxWidth(),
        targetState = suggestions,
        label = "IngredientSuggestionAnimation",
        contentAlignment = Alignment.TopCenter,
        transitionSpec = {
            slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn() togetherWith
                    slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
        }
    ) { suggestions ->
        RecipeIngredientSuggestion(
            modifier = if (isEditing) Modifier.padding(bottom = 4.dp) else Modifier,
            suggestions = suggestions,
            onSuggestionItemSelected = onSuggestionItemSelected,
            onDeleteSuggestion = onDeleteSuggestion
        )
    }
}

@Preview
@Composable
private fun PreviewRecipeAddTextField() {
    MaterialTheme {
        RecipeAddTextField(
            value = "This is some test value.",
            onValueChange = {},
            onDoneClick = {},
            hintText = "This is a hint",
            isEditing = false,
            suggestions = emptyList(),
            onSuggestionItemSelected = {},
            onDeleteSuggestion = {}
        )
    }
}

@Preview
@Composable
private fun PreviewRecipeAddTextFieldIsEditing() {
    MaterialTheme {
        RecipeAddTextField(
            value = "This is some test value.",
            onValueChange = {},
            onDoneClick = {},
            hintText = "This is a hint",
            isEditing = true,
            suggestions = listOf(Ingredient(1, "Ingredient 1"), Ingredient(2, "Ingredient 2")),
            onSuggestionItemSelected = {},
            onDeleteSuggestion = {}
        )
    }
}
