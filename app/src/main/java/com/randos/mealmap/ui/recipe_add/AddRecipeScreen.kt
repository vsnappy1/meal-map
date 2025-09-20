package com.randos.mealmap.ui.recipe_add

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.randos.domain.model.Ingredient
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.domain.type.RecipeHeaviness
import com.randos.domain.type.RecipeTag
import com.randos.mealmap.utils.Constants.RECIPE_INGREDIENT_QUANTITY_MAX_LENGTH
import com.randos.mealmap.utils.Utils
import com.randos.mealmap.utils.findActivity
import java.io.File

@Composable
fun AddRecipeScreen(
    id: Long? = null,
    onSaved: () -> Unit,
    viewModel: AddRecipeScreenViewModel = hiltViewModel<AddRecipeScreenViewModel>()
) {
    val state = viewModel.state.observeAsState(AddRecipeScreenState())
    AddRecipeScreen(
        id = id,
        state = state.value,
        onSave = {
            viewModel.onSave(onSaved = { onSaved() })
        },
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onImagePathChange = viewModel::onImagePathChange,
        onIngredientTextChange = viewModel::onIngredientTextChange,
        onIngredientAdd = viewModel::onIngredientAdd,
        onIngredientUpdateQuantity = viewModel::onIngredientUpdateQuantity,
        onIngredientUpdateUnit = viewModel::onIngredientUpdateUnit,
        onUpdateIngredient = viewModel::onUpdateIngredient,
        onDeleteIngredient = viewModel::onDeleteIngredient,
        onInstructionTextChange = viewModel::onInstructionTextChange,
        onInstructionAdd = viewModel::onInstructionAdd,
        onUpdateInstruction = viewModel::onUpdateInstruction,
        onDeleteInstruction = viewModel::onDeleteInstruction,
        onServingsChange = viewModel::onServingsChange,
        onPrepTimeChange = viewModel::onPrepTimeChange,
        onCookTimeChange = viewModel::onCookTimeChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onHeavinessChange = viewModel::onHeavinessChange,
        onTagChange = viewModel::onTagChange
    )
}

@Composable
private fun AddRecipeScreen(
    id: Long? = null,
    state: AddRecipeScreenState,
    onSave: () -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onImagePathChange: (String) -> Unit = {},
    onIngredientTextChange: (String) -> Unit = {},
    onIngredientAdd: (String) -> Unit = {},
    onIngredientUpdateQuantity: (Long, Double) -> Unit = { _, _ -> },
    onIngredientUpdateUnit: (Long, IngredientUnit?) -> Unit = { _, _ -> },
    onUpdateIngredient: (Ingredient) -> Unit = {},
    onDeleteIngredient: (Ingredient) -> Unit = {},
    onInstructionTextChange: (String) -> Unit = {},
    onInstructionAdd: (String) -> Unit = {},
    onUpdateInstruction: (Int, String) -> Unit = { _, _ -> },
    onDeleteInstruction: (Int) -> Unit = {},
    onServingsChange: (Int) -> Unit = {},
    onPrepTimeChange: (String) -> Unit = {},
    onCookTimeChange: (String) -> Unit = {},
    onCaloriesChange: (String) -> Unit = {},
    onHeavinessChange: (RecipeHeaviness) -> Unit = {},
    onTagChange: (RecipeTag) -> Unit = {}
) { // If id is provided, it's an edit action
    val recipe = state.recipe
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            RecipeImage(
                imagePath = recipe.imagePath,
                onImagePick = onImagePathChange,
                onCameraCapture = onImagePathChange
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
                value = state.currentIngredientText,
                onValueChange = onIngredientTextChange,
                onAdd = onIngredientAdd,
                onUpdateQuantity = onIngredientUpdateQuantity,
                onUpdateUnit = onIngredientUpdateUnit,
                ingredients = state.recipe.ingredients,
                onUpdateIngredient = onUpdateIngredient,
                onDeleteIngredient = onDeleteIngredient
            )
            Text(modifier = Modifier.padding(vertical = 4.dp), text = "Instructions")
            RecipeInstructions(
                value = state.currentInstructionText,
                onValueChange = onInstructionTextChange,
                onAdd = onInstructionAdd,
                onUpdateInstruction = onUpdateInstruction,
                onDeleteInstruction = onDeleteInstruction,
                instructions = state.recipe.instructions
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
            Servings(servings = recipe.servings, onServingsChange = onServingsChange)
            Tag(tag = recipe.tag, onTagChange = onTagChange)
            Heaviness(heaviness = recipe.heaviness, onHeavinessChange = onHeavinessChange)
            RecipeTextField(
                value = recipe.calories?.toString().orEmpty(),
                onValueChange = { onCaloriesChange(it) },
                label = "Total calories",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        }
        Header(
            title = state.recipe.title,
            onSave = onSave,
            isEdit = id != null
        )
    }
}

@Composable
private fun Servings(
    servings: Int?,
    onServingsChange: (Int) -> Unit
) {
    CustomDropdownMenu(
        value = servings,
        onValueChange = onServingsChange,
        hint = "Servings",
        items = Utils.servings,
        getTextValue = { it.toString() })
}

@Composable
private fun Tag(
    tag: RecipeTag?,
    onTagChange: (RecipeTag) -> Unit
) {
    CustomDropdownMenu(
        value = tag,
        onValueChange = onTagChange,
        hint = "Tag",
        items = Utils.recipeTags,
        getTextValue = { it.value })
}

@Composable
private fun Heaviness(
    heaviness: RecipeHeaviness?,
    onHeavinessChange: (RecipeHeaviness) -> Unit
) {
    CustomDropdownMenu(
        value = heaviness,
        onValueChange = onHeavinessChange,
        hint = "Heaviness",
        items = Utils.recipeHeaviness,
        getTextValue = { it.value })
}

@Composable
private fun <T> CustomDropdownMenu(
    value: T?,
    onValueChange: (T) -> Unit,
    hint: String,
    items: List<T>,
    getTextValue: (T) -> String
) {
    Spacer(modifier = Modifier.height(if (value != null) 4.dp else 8.dp))
    AnimatedVisibility(
        visible = value != null,
        enter = fadeIn() + expandVertically(),
    ) {
        Text(modifier = Modifier.padding(bottom = 4.dp), text = hint)
    }
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        onClick = { isExpanded = !isExpanded },
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (value == null) hint else getTextValue(value),
                textAlign = TextAlign.Center
            )
            Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null)
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                items.forEach {
                    DropdownMenuItem(
                        text = { Text(text = getTextValue(it)) },
                        onClick = {
                            onValueChange(it)
                            isExpanded = false
                        })
                }
            }
        }
    }
}

@Composable
fun RecipeInstructions(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onUpdateInstruction: (Int, String) -> Unit,
    onDeleteInstruction: (Int) -> Unit,
    instructions: List<String>
) {
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
                    index = index
                )
                HorizontalDivider(modifier = Modifier)
            }
            AddTextField(
                modifier = Modifier.padding(top = 4.dp),
                value = value,
                onValueChange = onValueChange,
                onIngredientAdd = onAdd,
                hintText = "Step ${instructions.size + 1}"
            )
        }
    }
}

@Composable
private fun RecipeIngredients(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    onAdd: (String) -> Unit,
    onUpdateIngredient: (Ingredient) -> Unit,
    onDeleteIngredient: (Ingredient) -> Unit,
    onUpdateQuantity: (Long, Double) -> Unit,
    onUpdateUnit: (Long, IngredientUnit?) -> Unit,
    ingredients: List<RecipeIngredient>
) {
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
                    onUpdateQuantity = onUpdateQuantity,
                    onUpdateUnit = onUpdateUnit,
                    onUpdateName = { newName -> onUpdateIngredient(ingredient.ingredient.copy(name = newName)) },
                    onDelete = { onDeleteIngredient(ingredient.ingredient.copy(id = it)) },
                    hintText = "Ingredient ${index + 1}"

                )
                HorizontalDivider(modifier = Modifier)
            }
            AddTextField(
                modifier = Modifier.padding(top = 4.dp),
                value = value,
                onValueChange = onValueChange,
                onIngredientAdd = onAdd,
                hintText = "Ingredient ${ingredients.size + 1}"
            )
        }
    }
}

@Composable
private fun RecipeIngredient(
    modifier: Modifier = Modifier,
    ingredient: RecipeIngredient,
    onUpdateName: (String) -> Unit,
    onUpdateQuantity: (Long, Double) -> Unit,
    onUpdateUnit: (Long, IngredientUnit?) -> Unit,
    onDelete: (Long) -> Unit,
    hintText: String = ""
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isEditingIngredient by remember { mutableStateOf(false) }
    if (isEditingIngredient) {
        var value by remember { mutableStateOf(ingredient.ingredient.name) }
        var hadFocus by remember { mutableStateOf(false) }
        AddTextField(
            value = value,
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
                        isEditingIngredient = false // exit edit mode after user taps away
                    }

                    // Keep track of current focus state for next comparison
                    hadFocus = state.isFocused
                },
            onValueChange = { value = it },
            onIngredientAdd = {
                onUpdateName(it)
                isEditingIngredient = false
            },
            isEditing = true,
            hintText = hintText
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        return
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    isEditingIngredient = true
                },
            text = ingredient.ingredient.name,
        )
        QuantityTextField(
            ingredient = ingredient,
            onUpdateQuantity = onUpdateQuantity,
            onKeyboardDone = {
                focusManager.clearFocus()
            }
        )
        UnitDropDown(
            modifier = Modifier.width(60.dp),
            onUnitChange = {
                onUpdateUnit(ingredient.ingredient.id, it)
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

@Composable
private fun QuantityTextField(
    ingredient: RecipeIngredient,
    onUpdateQuantity: (Long, Double) -> Unit,
    onKeyboardDone: () -> Unit
) {
    val quantityString = if (ingredient.quantity % 1.0 == 0.0) {
        ingredient.quantity.toInt().toString()
    } else {
        ingredient.quantity.toString()
    }
    var quality by remember { mutableStateOf(quantityString) }

    BasicTextField(
        modifier = Modifier
            .width(50.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(vertical = 2.dp),
        singleLine = true,
        value = quality,
        onValueChange = {
            if (it.length > RECIPE_INGREDIENT_QUANTITY_MAX_LENGTH) return@BasicTextField
            quality = it
            onUpdateQuantity(
                ingredient.ingredient.id,
                quality.toDoubleOrNull() ?: 0.0
            )
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        keyboardActions = KeyboardActions(onDone = {
            if (quality.toDoubleOrNull() == null) {
                quality = "0.0"
            }
            onKeyboardDone()
        }),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            modifier = Modifier.alpha(if (quality.isEmpty()) 1f else 0f),
            textAlign = TextAlign.Center,
            text = "Qty",
            color = MaterialTheme.colorScheme.outline,
        )
        it()
    }
}

@Composable
private fun RecipeInstruction(
    modifier: Modifier = Modifier,
    instruction: String,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit,
    index: Int
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isEditingIngredient by remember { mutableStateOf(false) }
    if (isEditingIngredient) {
        var value by remember { mutableStateOf(instruction) }
        var hadFocus by remember { mutableStateOf(false) }
        AddTextField(
            value = value,
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
                        isEditingIngredient = false // exit edit mode after user taps away
                    }

                    // Keep track of current focus state for next comparison
                    hadFocus = state.isFocused
                },
            onValueChange = { value = it },
            onIngredientAdd = {
                onUpdate(it)
                isEditingIngredient = false
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    isEditingIngredient = true
                },
            text = "${index + 1}. $instruction",
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

@Composable
private fun AddTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onIngredientAdd: (String) -> Unit,
    hintText: String = "",
    isEditing: Boolean = false
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
                    onIngredientAdd(value)
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
                onIngredientAdd(value)
                focusManager.clearFocus()
            },
            enabled = value.isNotEmpty(),
            colors = iconButtonColors()
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
            )
        }
    }
    if (isEditing) return
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun iconButtonColors() = IconButtonDefaults.iconButtonColors()
    .copy(
        containerColor = ButtonDefaults.buttonColors().containerColor,
        contentColor = ButtonDefaults.buttonColors().contentColor,
        disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
        disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
    )

@Composable
private fun UnitDropDown(
    modifier: Modifier = Modifier,
    onUnitChange: (IngredientUnit) -> Unit,
    ingredientUnit: IngredientUnit?
) {
    var isExpanded by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(ingredientUnit) }
    Surface(
        modifier = modifier
            .clickable { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .weight(1f),
                text = unit?.displayName ?: "Unit",
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            Utils.ingredientUnits.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.displayName) },
                    onClick = {
                        unit = it
                        isExpanded = false
                        onUnitChange(it)
                    })
            }
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
        modifier = modifier.fillMaxWidth(),
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
private fun RecipeImage(
    imagePath: String?,
    onImagePick: (String) -> Unit,
    onCameraCapture: (String) -> Unit
) {
    var showImageSourceDialog by remember { mutableStateOf(false) }
    if (showImageSourceDialog) {
        ImageCapture(
            onDismiss = {
                showImageSourceDialog = false
            },
            onImagePick = {
                onImagePick(it)
                showImageSourceDialog = false
            },
            onCameraCapture = {
                onCameraCapture(it)
                showImageSourceDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (imagePath == null) {
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(7)) {
                    for (i in 0..5) {
                        items(Utils.foodIcons.size) { index ->
                            Icon(
                                imageVector = Utils.foodIcons[(i + index) % 7],
                                contentDescription = null
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.small
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable {
                            showImageSourceDialog = true
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    text = "Add Image",
                )
            } else {
                AsyncImage(
                    model = imagePath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(35.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        showImageSourceDialog = true
                    },
                    colors = iconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun permissionLauncher(
    context: Context,
    capturedImageUri: Uri?,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
): ManagedActivityResultLauncher<String, Boolean> = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        capturedImageUri?.let { cameraLauncher.launch(it) }
    } else {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun cameraLauncher(
    context: Context,
    capturedImageUri: Uri?,
    onCameraCapture: (String) -> Unit
): ManagedActivityResultLauncher<Uri, Boolean> =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            capturedImageUri?.let { onCameraCapture(it.toString()) }
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

@Composable
private fun imagePickerLauncher(onImagePick: (String) -> Unit): ManagedActivityResultLauncher<String, Uri?> =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onImagePick(it.toString()) }
    }

private fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "meal_map_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

private fun requestCameraPermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {},
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val cameraPermission = Manifest.permission.CAMERA
    when {
        ContextCompat.checkSelfPermission(context, cameraPermission) ==
                PackageManager.PERMISSION_GRANTED -> {
            // Already granted
            onGranted()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity(),
            cameraPermission
        ) -> {
            // User previously denied → show rationale UI
            onDenied()
        }

        else -> {
            // Request permission
            permissionLauncher.launch(cameraPermission)
        }
    }
}

@Composable
private fun ImageCapture(
    onDismiss: () -> Unit,
    onImagePick: (String) -> Unit,
    onCameraCapture: (String) -> Unit
) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imagePicker = imagePickerLauncher(onImagePick)
    val cameraLauncher = cameraLauncher(context, capturedImageUri, onCameraCapture)
    val permissionLauncher = permissionLauncher(context, capturedImageUri, cameraLauncher)

    ImageSourceDialog(
        onDismiss = onDismiss,
        onGalleryClick = {
            imagePicker.launch("image/*")
        },
        onCameraClick = {
            val uri = createImageUri(context)
            capturedImageUri = uri
            requestCameraPermission(
                context = context,
                permissionLauncher = permissionLauncher,
                onGranted = {
                    cameraLauncher.launch(uri)
                },
                onDenied = {
                    Toast.makeText(
                        context,
                        "Go to app settings and enable camera permission",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onDismiss()
                }
            )
        })
}

@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Choose Image Source", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Gallery")
                }
                Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Camera")
                }
            }
        }
    }
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
                enabled = title.isNotEmpty()
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
