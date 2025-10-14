package com.randos.mealmap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Ingredient
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit

@Composable
fun RecipeIngredient(
    modifier: Modifier = Modifier,
    ingredient: RecipeIngredient,
    onUpdateName: (String) -> Unit,
    editText: String,
    onEditTextChanged: (String) -> Unit,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit,
    onUpdateQuantity: (Double) -> Unit,
    onUpdateUnit: (IngredientUnit?) -> Unit,
    onDelete: (Long) -> Unit,
    hintText: String = "",
    suggestions: List<Ingredient>,
    onSuggestionItemSelected: (Ingredient) -> Unit,
    onDeleteSuggestion: (Ingredient) -> Unit
) {
    Column {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        if (isEditing) {
            var hadFocus by remember { mutableStateOf(false) }
            RecipeAddTextField(
                value = editText,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { state ->
                        // 🔹 Problem: onFocusChanged is always called at least once with isFocused = false
                        // right after the TextField is composed. If we exit edit mode here, the user
                        // never gets a chance to actually edit.
                        //
                        // 🔹 Solution: Only exit edit mode when focus was previously gained (hadFocus = true)
                        // and is now lost (isFocused = false). This ensures we ignore the "initial false"
                        // event that fires before requestFocus() succeeds.

                        if (hadFocus && !state.isFocused) {
                            onIsEditingChange(false) // exit edit mode after user taps away
                        }

                        // Keep track of current focus state for next comparison
                        hadFocus = state.isFocused
                    },
                onValueChange = onEditTextChanged,
                onDoneClick = {
                    onUpdateName(it)
                    onIsEditingChange(false)
                },
                isEditing = true,
                hintText = hintText,
                suggestions = suggestions,
                onSuggestionItemSelected = onSuggestionItemSelected,
                onDeleteSuggestion = onDeleteSuggestion
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            return
        }
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onIsEditingChange(true)
                    },
                text = ingredient.ingredient.name
            )
            RecipeQuantityTextField(
                ingredient = ingredient,
                onUpdateQuantity = onUpdateQuantity,
                onKeyboardDone = {
                    focusManager.clearFocus()
                }
            )
            RecipeUnitDropDown(
                modifier = Modifier.width(60.dp),
                onUnitChange = {
                    onUpdateUnit(it)
                },
                ingredientUnit = ingredient.unit
            )
            IconButton(
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .align(Alignment.Top),
                onClick = {
                    onDelete(ingredient.ingredient.id)
                    focusManager.clearFocus()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRecipeIngredientEditing() {
    MaterialTheme {
        RecipeIngredient(
            ingredient = RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient A"),
                unit = IngredientUnit.GRAM,
                quantity = 1.0
            ),
            onUpdateName = {},
            editText = "Ingredient A",
            onEditTextChanged = {},
            isEditing = true,
            onIsEditingChange = {},
            onUpdateQuantity = {},
            onUpdateUnit = {},
            onDelete = {},
            suggestions = listOf(),
            onSuggestionItemSelected = {},
            onDeleteSuggestion = {}
        )
    }
}

@Preview
@Composable
private fun PreviewRecipeIngredient() {
    MaterialTheme {
        RecipeIngredient(
            ingredient = RecipeIngredient(
                ingredient = Ingredient(name = "Ingredient A"),
                unit = IngredientUnit.GRAM,
                quantity = 1.0
            ),
            onUpdateName = {},
            editText = "Ingredient A",
            onEditTextChanged = {},
            isEditing = false,
            onIsEditingChange = {},
            onUpdateQuantity = {},
            onUpdateUnit = {},
            onDelete = {},
            suggestions = listOf(),
            onSuggestionItemSelected = {},
            onDeleteSuggestion = {}
        )
    }
}
