package com.randos.mealmap.ui.recipes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.randos.domain.type.RecipeTag
import com.randos.domain.type.RecipesSort
import com.randos.domain.type.SortOrder
import com.randos.mealmap.R
import com.randos.mealmap.ui.components.RecipeItem
import com.randos.mealmap.ui.components.RecipePill
import com.randos.mealmap.utils.Utils

@Composable
fun RecipesScreen(
    onAddNewRecipe: () -> Unit,
    onRecipeClick: (id: Long) -> Unit,
    viewModel: RecipesScreenViewModel = hiltViewModel<RecipesScreenViewModel>()
) {
    val state by viewModel.state.observeAsState(RecipesScreenState())
    RecipesScreen(
        onAddNewRecipe = onAddNewRecipe,
        onRecipeClick = onRecipeClick,
        state = state,
        onSearchTextChange = viewModel::onSearchTextChange,
        onSortChange = viewModel::onSortChange,
        onFilterChange = viewModel::onFilterChange,
        onSortOrderChange = viewModel::onSortOrderChange
    )
    LaunchedEffect(Unit) {
        viewModel.getRecipes()
    }
}

@Composable
private fun RecipesScreen(
    onAddNewRecipe: () -> Unit,
    onRecipeClick: (id: Long) -> Unit,
    state: RecipesScreenState,
    onSearchTextChange: (String) -> Unit,
    onSortChange: (RecipesSort?) -> Unit,
    onFilterChange: (RecipeTag?) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recipes",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(64.dp))
        SearchBar(
            text = state.searchText,
            onSearchTextChange = onSearchTextChange
        )
        ActionButtons(
            state = state,
            onFilterChange = onFilterChange,
            onSortChange = onSortChange,
            onAddNewRecipe = onAddNewRecipe,
            onSortOrderChange = onSortOrderChange
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            state.recipes.forEach { recipe ->
                item {
                    RecipeItem(
                        recipe = recipe,
                        onClick = onRecipeClick
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun SortOrder(
    state: RecipesScreenState,
    onSortOrderChange: (SortOrder) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                if (state.sortOrder == SortOrder.ASCENDING) {
                    onSortOrderChange(SortOrder.DESCENDING)
                } else {
                    onSortOrderChange(SortOrder.ASCENDING)
                }
            }) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Icons.Rounded.KeyboardArrowUp,
            contentDescription = stringResource(R.string.sort_ascending),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = if (state.sortOrder == SortOrder.ASCENDING) 1f else 0.3f)
        )
        Icon(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(32.dp),
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = stringResource(R.string.sort_descending),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = if (state.sortOrder == SortOrder.DESCENDING) 1f else 0.3f)
        )
    }
}

@Composable
private fun ActionButtons(
    state: RecipesScreenState,
    onFilterChange: (RecipeTag?) -> Unit,
    onSortChange: (RecipesSort?) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onAddNewRecipe: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropDownButton(
            text = "Filter",
            items = Utils.recipeTags,
            onItemSelect = { onFilterChange(it as? RecipeTag) },
            displayValue = { (it as RecipeTag).value },
            selectedItem = state.filter
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownButton(
            text = "Sort by",
            items = Utils.recipeSort,
            onItemSelect = { onSortChange(it) },
            displayValue = { it.value },
            selectedItem = state.sort
        )
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedVisibility(state.sort != null) {
            SortOrder(state = state, onSortOrderChange = onSortOrderChange)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onAddNewRecipe) { Text(text = "Add") }
    }
}

@Composable
private fun SearchBar(
    text: String,
    onSearchTextChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { onSearchTextChange(it) },
        label = { Text(text = "Search") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        trailingIcon = {
            AnimatedContent(
                targetState = text.isEmpty(),
                label = "SearchClearIcon",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { isEmpty ->
                if (isEmpty) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = stringResource(R.string.clear_text),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onSearchTextChange("")
                                focusManager.clearFocus()
                            }
                    )
                }
            }
        })
}

@Composable
private fun <T> DropDownButton(
    modifier: Modifier = Modifier,
    text: String,
    items: List<T>,
    displayValue: (T) -> String,
    onItemSelect: (T?) -> Unit,
    selectedItem: T? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text)
            AnimatedVisibility(visible = selectedItem != null) {
                Spacer(
                    modifier = Modifier
                        .size(height = 2.dp, width = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
        if (isExpanded) {
            DropdownMenu(
                onDismissRequest = { isExpanded = false },
                items = items,
                selectedItem = selectedItem,
                onItemSelect = onItemSelect,
                displayValue = displayValue
            )
        }
    }
}

@Composable
private fun <T> DropdownMenu(
    onDismissRequest: () -> Unit,
    items: List<T>,
    selectedItem: T?,
    onItemSelect: (T?) -> Unit,
    displayValue: (T) -> String
) {
    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismissRequest,
        offset = IntOffset(0, 55)
    ) {
        FlowRow(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->
                val isItemSelected = item == selectedItem
                RecipePill(
                    isSelected = isItemSelected,
                    onItemSelect = {
                        onItemSelect(it)
                        onDismissRequest()
                    },
                    item = item,
                    displayValue = displayValue
                )
            }
            Text(
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 6.dp)
                    .clickable {
                        onItemSelect(null)
                        onDismissRequest()
                    },
                text = "Clear",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipesScreenPreview() {
    RecipesScreen(
        onAddNewRecipe = { },
        onRecipeClick = {},
        state = RecipesScreenState(recipes = Utils.recipes),
        onSearchTextChange = {},
        onSortChange = {},
        onFilterChange = {},
        onSortOrderChange = {}
    )
}
