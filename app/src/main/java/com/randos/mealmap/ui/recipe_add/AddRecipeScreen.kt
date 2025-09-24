package com.randos.mealmap.ui.recipe_add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.randos.domain.model.Ingredient
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.mealmap.ui.components.CustomDropdownMenu
import com.randos.mealmap.ui.components.RecipeAddTextField
import com.randos.mealmap.ui.components.RecipeImage
import com.randos.mealmap.ui.components.RecipeIngredient
import com.randos.mealmap.ui.components.RecipeInstruction
import com.randos.mealmap.ui.components.RecipePill
import com.randos.mealmap.ui.theme.buttonColors
import com.randos.mealmap.utils.Utils
import com.randos.mealmap.utils.Utils.copyUriToAppStorage
import kotlinx.coroutines.launch

@Composable
fun AddRecipeScreen(
    id: Long? = null,
    onSaved: () -> Unit,
    viewModel: AddRecipeScreenViewModel = hiltViewModel<AddRecipeScreenViewModel>()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.observeAsState(AddRecipeScreenState(isLoading = id != null))
    var shouldMakeCopyOfImage by remember { mutableStateOf(false) }
    if (state.value.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    AddRecipeScreen(
        id = id,
        state = state.value,
        onSave = {
            if (shouldMakeCopyOfImage) {
                val uri = state.value.recipe.imagePath?.toUri()
                coroutineScope.launch {
                    val path = uri?.let { copyUriToAppStorage(context, it) }
                    viewModel.onSave(path.toString(), onSaved = { onSaved() })
                }
            } else {
                viewModel.onSave(onSaved = { onSaved() })
            }
        },
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onImagePathChange = { url, shouldCopy ->
            viewModel.onImagePathChange(url)
            shouldMakeCopyOfImage = shouldCopy
        },
        onIngredientAdd = viewModel::onIngredientAdd,
        onIngredientUpdate = viewModel::onIngredientUpdate,
        onIngredientDelete = viewModel::onIngredientDelete,
        onIngredientUpdateUnit = viewModel::onIngredientUpdateUnit,
        onIngredientUpdateQuantity = viewModel::onIngredientUpdateQuantity,
        onIngredientTextChange = viewModel::onIngredientTextChange,
        onIngredientEditTextChanged = viewModel::onIngredientEditTextChanged,
        onIngredientIsEditingChange = viewModel::onIngredientIsEditingChange,
        onInstructionAdd = viewModel::onInstructionAdd,
        onInstructionUpdate = viewModel::onInstructionUpdate,
        onInstructionDelete = viewModel::onInstructionDelete,
        onInstructionTextChange = viewModel::onInstructionTextChange,
        onInstructionEditTextChanged = viewModel::onInstructionEditTextChanged,
        onInstructionIsEditingChange = viewModel::onInstructionIsEditingChange,
        onServingsChange = viewModel::onServingsChange,
        onPrepTimeChange = viewModel::onPrepTimeChange,
        onCookTimeChange = viewModel::onCookTimeChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onHeavinessChange = viewModel::onHeavinessChange,
        onTagClick = viewModel::onTagClick,
        onSuggestionItemSelected = viewModel::onSuggestionItemSelected,
        onDeleteSuggestedIngredient = viewModel::onDeleteSuggestedIngredient
    )

    LaunchedEffect(state.value.errorMessage) {
        if (state.value.errorMessage == null) return@LaunchedEffect
        Toast.makeText(context, state.value.errorMessage, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun AddRecipeScreen(
    id: Long? = null,
    state: AddRecipeScreenState,
    onSave: () -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onImagePathChange: (String, Boolean) -> Unit = { _, _ -> },
    onIngredientAdd: (String) -> Unit = {},
    onIngredientUpdate: (Int, String) -> Unit = { _, _ -> },
    onIngredientDelete: (Ingredient) -> Unit = {},
    onIngredientUpdateUnit: (Int, IngredientUnit?) -> Unit = { _, _ -> },
    onIngredientUpdateQuantity: (Int, Double) -> Unit = { _, _ -> },
    onIngredientTextChange: (String) -> Unit = {},
    onIngredientEditTextChanged: (String) -> Unit = {},
    onIngredientIsEditingChange: (Int, Boolean) -> Unit = { _, _ -> },
    onInstructionAdd: (String) -> Unit = {},
    onInstructionUpdate: (Int, String) -> Unit = { _, _ -> },
    onInstructionDelete: (Int) -> Unit = {},
    onInstructionTextChange: (String) -> Unit = {},
    onInstructionEditTextChanged: (String) -> Unit = {},
    onInstructionIsEditingChange: (Int, Boolean) -> Unit = { _, _ -> },
    onServingsChange: (Int) -> Unit = {},
    onPrepTimeChange: (String) -> Unit = {},
    onCookTimeChange: (String) -> Unit = {},
    onCaloriesChange: (String) -> Unit = {},
    onHeavinessChange: (RecipeHeaviness) -> Unit = {},
    onTagClick: (RecipeTag) -> Unit = {},
    onSuggestionItemSelected: (Int, Ingredient) -> Unit = { _, _ -> },
    onDeleteSuggestedIngredient: (Ingredient) -> Unit = {},
) {
    val recipe = state.recipe
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            RecipeImage(
                imagePath = recipe.imagePath,
                onImagePick = { onImagePathChange(it, true) },
                onCameraCapture = { onImagePathChange(it, false) }
            )
            RecipeTextField(
                value = recipe.title,
                onValueChange = onTitleChange,
                label = "Title*"
            )
            RecipeTextField(
                value = recipe.description.orEmpty(),
                onValueChange = onDescriptionChange,
                label = "Description",
                maxLines = 3,
                imeAction = ImeAction.Done
            )
            Text(modifier = Modifier.padding(vertical = 4.dp), text = "Ingredients")
            RecipeIngredients(
                state = state,
                onValueChange = onIngredientTextChange,
                onAdd = onIngredientAdd,
                onUpdateQuantity = onIngredientUpdateQuantity,
                onUpdateUnit = onIngredientUpdateUnit,
                onUpdateIngredient = onIngredientUpdate,
                onDeleteIngredient = onIngredientDelete,
                onSuggestionItemSelected = onSuggestionItemSelected,
                onEditTextChanged = onIngredientEditTextChanged,
                onIsEditingChange = onIngredientIsEditingChange,
                onDeleteSuggestion = onDeleteSuggestedIngredient
            )
            Text(modifier = Modifier.padding(vertical = 4.dp), text = "Instructions")
            RecipeInstructions(
                state = state,
                onValueChange = onInstructionTextChange,
                onAdd = onInstructionAdd,
                onUpdateInstruction = onInstructionUpdate,
                onDeleteInstruction = onInstructionDelete,
                onEditTextChanged = onInstructionEditTextChanged,
                onIsEditingChange = onInstructionIsEditingChange
            )

            RecipeTextField(
                value = recipe.prepTime?.toString().orEmpty(),
                onValueChange = { onPrepTimeChange(it) },
                label = "Prep Time (minutes)",
                keyboardType = KeyboardType.Number
            )
            RecipeTextField(
                value = recipe.cookTime?.toString().orEmpty(),
                onValueChange = { onCookTimeChange(it) },
                label = "Cook Time (minutes)",
                keyboardType = KeyboardType.Number
            )
            Tag(selectedTags = recipe.tags, onTagClick = onTagClick)
            Servings(servings = recipe.servings, onServingsChange = onServingsChange)
            Heaviness(heaviness = recipe.heaviness, onHeavinessChange = onHeavinessChange)
            RecipeTextField(
                value = recipe.calories?.toString().orEmpty(),
                onValueChange = { onCaloriesChange(it) },
                label = "Total calories",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Header(
            title = state.recipe.title,
            onSave = onSave,
            isEdit = id != null
        )
    }
}

@Composable
private fun Servings(servings: Int?, onServingsChange: (Int) -> Unit) {
    CustomDropdownMenu(
        value = servings,
        onValueChange = onServingsChange,
        hint = "Servings",
        items = Utils.servings,
        getTextValue = { it.toString() })
}

@Composable
private fun Tag(
    selectedTags: List<RecipeTag>,
    onTagClick: (RecipeTag) -> Unit,
) {
    Text(modifier = Modifier.padding(vertical = 4.dp), text = "Tags")
    Card {
        FlowRow(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Utils.recipeTags.forEach { item ->
                RecipePill(
                    isSelected = selectedTags.contains(item),
                    onItemSelect = { onTagClick(it) },
                    item = item,
                    displayValue = { it.value }
                )
            }
        }
    }
}

@Composable
private fun Heaviness(heaviness: RecipeHeaviness?, onHeavinessChange: (RecipeHeaviness) -> Unit) {
    CustomDropdownMenu(
        value = heaviness,
        onValueChange = onHeavinessChange,
        hint = "Heaviness",
        items = Utils.recipeHeaviness,
        getTextValue = { it.value })
}

@Composable
fun RecipeInstructions(
    modifier: Modifier = Modifier,
    state: AddRecipeScreenState,
    onValueChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onUpdateInstruction: (Int, String) -> Unit,
    onDeleteInstruction: (Int) -> Unit,
    onEditTextChanged: (String) -> Unit,
    onIsEditingChange: (Int, Boolean) -> Unit,
) {
    val instructions = state.recipe.instructions
    Card(
        modifier = modifier
            .wrapContentHeight(unbounded = true)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),

            ) {
            instructions.forEachIndexed { index, instruction ->
                RecipeInstruction(
                    modifier = Modifier.padding(vertical = 4.dp),
                    instruction = instruction,
                    onUpdate = { onUpdateInstruction(index, it) },
                    onDelete = { onDeleteInstruction(index) },
                    index = index,
                    isEditing = state.editInstructionIndex == index,
                    onIsEditingChange = { onIsEditingChange(index, it) },
                    editText = state.editInstructionText,
                    onEditTextChanged = onEditTextChanged
                )
                HorizontalDivider(modifier = Modifier)
            }
            RecipeAddTextField(
                modifier = Modifier.padding(top = 4.dp),
                value = state.currentInstructionText,
                onValueChange = onValueChange,
                onDoneClick = onAdd,
                hintText = "Step ${instructions.size + 1}"
            )
        }
    }
}

@Composable
private fun RecipeIngredients(
    modifier: Modifier = Modifier,
    state: AddRecipeScreenState,
    onValueChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onUpdateIngredient: (Int, String) -> Unit,
    onDeleteIngredient: (Ingredient) -> Unit,
    onUpdateUnit: (Int, IngredientUnit?) -> Unit,
    onUpdateQuantity: (Int, Double) -> Unit,
    onEditTextChanged: (String) -> Unit,
    onIsEditingChange: (Int, Boolean) -> Unit,
    onSuggestionItemSelected: (Int, Ingredient) -> Unit,
    onDeleteSuggestion: (Ingredient) -> Unit,
) {
    val ingredients = state.recipe.ingredients
    val suggestions = state.ingredientSuggestions
    Card(
        modifier = modifier
            .wrapContentHeight(unbounded = true)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),

            ) {
            ingredients.forEachIndexed { index, ingredient ->
                RecipeIngredient(
                    modifier = Modifier.padding(vertical = 4.dp),
                    ingredient = ingredient,
                    onUpdateName = { onUpdateIngredient(index, it) },
                    editText = state.editIngredientText,
                    onEditTextChanged = onEditTextChanged,
                    isEditing = state.editIngredientIndex == index,
                    onIsEditingChange = { onIsEditingChange(index, it) },
                    onUpdateQuantity = { onUpdateQuantity(index, it) },
                    onUpdateUnit = { onUpdateUnit(index, it) },
                    onDelete = { onDeleteIngredient(ingredient.ingredient.copy(id = it)) },
                    hintText = "Ingredient ${index + 1}",
                    suggestions = suggestions,
                    onSuggestionItemSelected = { onSuggestionItemSelected(index, it) },
                    onDeleteSuggestion = onDeleteSuggestion
                )
                HorizontalDivider(modifier = Modifier)
            }
            RecipeAddTextField(
                modifier = Modifier.padding(top = 4.dp),
                value = state.currentIngredientText,
                onValueChange = onValueChange,
                onDoneClick = onAdd,
                hintText = "Ingredient ${ingredients.size + 1}",
                // We don't want to show suggestions on this text filed while some of the ingredients are being edited
                suggestions = if (state.editIngredientIndex == null && state.currentIngredientText.isNotEmpty()) suggestions else emptyList(),
                onSuggestionItemSelected = { onSuggestionItemSelected(ingredients.size, it) },
                onDeleteSuggestion = onDeleteSuggestion
            )
        }
    }
}

@Composable
private fun RecipeTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        value = value,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        shape = MaterialTheme.shapes.medium,
        onValueChange = onValueChange,
        label = { Text(label) }
    )
}

@Composable
private fun Header(title: String = "", onSave: () -> Unit, isEdit: Boolean) {
    val headerText = if (isEdit) "Edit Recipe" else "New Recipe"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = headerText, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSave,
                enabled = title.isNotEmpty(),
                colors = buttonColors(title.isNotEmpty())
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AddRecipeScreenPreview() {
    MaterialTheme {
        AddRecipeScreen(
            id = null,
            state = AddRecipeScreenState(),
        )
    }
}
