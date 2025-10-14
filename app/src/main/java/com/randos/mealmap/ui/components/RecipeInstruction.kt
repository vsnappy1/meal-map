package com.randos.mealmap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.randos.mealmap.ui.theme.MealMapTheme

@Composable
fun RecipeInstruction(
    modifier: Modifier = Modifier,
    instruction: String,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit,
    index: Int,
    editText: String,
    onEditTextChanged: (String) -> Unit,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit
) {
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
                onUpdate(it)
                onIsEditingChange(false)
            },
            isEditing = true,
            hintText = "Step ${index + 1}"
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
            text = "${index + 1}. $instruction"
        )
        IconButton(
            modifier = Modifier
                .size(24.dp)
                .padding(2.dp)
                .align(Alignment.Top),
            onClick = {
                onDelete()
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

@Preview
@Composable
private fun RecipeInstructionPreviewEditing() {
    MealMapTheme {
        RecipeInstruction(
            instruction = "instruction",
            onUpdate = {},
            onDelete = { },
            index = 0,
            editText = "editText",
            onEditTextChanged = {},
            isEditing = true,
            onIsEditingChange = {}
        )
    }
}

@Preview
@Composable
private fun RecipeInstructionPreview() {
    MealMapTheme {
        RecipeInstruction(
            instruction = "instruction",
            onUpdate = {},
            onDelete = { },
            index = 0,
            editText = "",
            onEditTextChanged = {},
            isEditing = false,
            onIsEditingChange = {}
        )
    }
}
