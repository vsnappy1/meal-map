package com.randos.mealmap.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.randos.domain.model.Meal
import com.randos.domain.model.Recipe
import com.randos.domain.type.MealType
import com.randos.mealmap.R
import com.randos.mealmap.ui.components.RecipeItemImage
import com.randos.mealmap.ui.components.RecipeSuggestion
import com.randos.mealmap.ui.components.VerticalAnimatedContent
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    onRecipeClick: (Recipe) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.observeAsState(HomeScreenState())
    HomeScreen(
        state = state.value,
        onIsSelectingWeekUpdate = viewModel::onIsSelectingWeekUpdate,
        onSelectedWeekTextUpdate = viewModel::onSelectedWeekTextUpdate,
        onCurrentMealEditingUpdate = viewModel::onCurrentMealEditingUpdate,
        onAddMeal = viewModel::onAddMeal,
        onRemoveMeal = viewModel::onRemoveMeal,
        onRecipeClick = onRecipeClick,
        onAddNewRecipe = viewModel::onAddNewRecipe
    )
    LaunchedEffect(Unit) {
        viewModel.getWeekPlan()
    }
}

@Composable
private fun HomeScreen(
    state: HomeScreenState = HomeScreenState(),
    onIsSelectingWeekUpdate: (Boolean) -> Unit = {},
    onSelectedWeekTextUpdate: (Int, String) -> Unit = { _, _ -> },
    onCurrentMealEditingUpdate: (Triple<LocalDate, MealType, String>?) -> Unit = {},
    onAddMeal: (Recipe, MealType, LocalDate) -> Unit = { _, _, _ -> },
    onRemoveMeal: (Recipe, MealType, LocalDate) -> Unit = { _, _, _ -> },
    onRecipeClick: (Recipe) -> Unit = {},
    onAddNewRecipe: (String, MealType, LocalDate) -> Unit = { _, _, _ -> }
) {
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            text = "Welcome User,",
            style = MaterialTheme.typography.headlineLarge
        )
        WeekSelector(
            isSelectingWeek = state.isSelectingWeek,
            selectedWeekText = state.selectedWeekText,
            weeksAvailable = state.weeksAvailable,
            onIsSelectingWeekUpdate = onIsSelectingWeekUpdate,
            onSelectedWeekTextUpdate = { week, text ->
                focusManager.clearFocus()
                onSelectedWeekTextUpdate(week, text)
            }
        )
        AnimatedVisibility(visible = state.isSelectingWeek) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        DateView(state.dateFrom, state.dateTo)
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.mealMap.entries.toList(),
                key = { (date, _) -> date }) { (date, meals) ->
                MealDay(
                    date = date,
                    meals = meals,
                    currentMealEditing = state.currentMealEditing,
                    recipeSuggestions = state.recipeSuggestions,
                    onCurrentMealEditingUpdate = onCurrentMealEditingUpdate,
                    onAddMeal = onAddMeal,
                    onRemoveMeal = onRemoveMeal,
                    onRecipeClick = onRecipeClick,
                    onAddNewRecipe = onAddNewRecipe
                )
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
    LaunchedEffect(Unit) {
        if (isFirstLaunch) {
            val index: Int = state.mealMap.entries.map { it.key }.indexOf(LocalDate.now())
            if (index == -1) return@LaunchedEffect
            delay(500)
            lazyListState.animateScrollToItem(index)
            isFirstLaunch = false
        }
    }
}

@Composable
private fun MealDay(
    date: LocalDate,
    meals: List<Meal>,
    currentMealEditing: Triple<LocalDate, MealType, String>?,
    recipeSuggestions: List<Recipe>,
    onCurrentMealEditingUpdate: (Triple<LocalDate, MealType, String>?) -> Unit,
    onAddMeal: (Recipe, MealType, LocalDate) -> Unit,
    onRemoveMeal: (Recipe, MealType, LocalDate) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onAddNewRecipe: (String, MealType, LocalDate) -> Unit
) {
    val map = meals
        .groupBy { it.type }
        .mapValues { it.value.firstOrNull()?.recipes ?: emptyList() }
    Text(
        text = date.getDayName(),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.W600
    )
    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MealRowItem(
            originalMealType = MealType.BREAKFAST,
            mealIconPainter = painterResource(R.drawable.icon_bread_and_coffee),
            date = date,
            currentMealEditing = currentMealEditing,
            recipes = map[MealType.BREAKFAST] ?: emptyList(),
            recipeSuggestions = recipeSuggestions,
            onCurrentMealEditingUpdate = onCurrentMealEditingUpdate,
            onAddMeal = onAddMeal,
            onRecipeClick = onRecipeClick,
            onRemoveMeal = onRemoveMeal,
            onAddNewRecipe = { onAddNewRecipe(it, MealType.BREAKFAST, date) }
        )
        MealRowItem(
            originalMealType = MealType.LUNCH,
            mealIconPainter = painterResource(R.drawable.icon_salad),
            date = date,
            currentMealEditing = currentMealEditing,
            recipes = map[MealType.LUNCH] ?: emptyList(),
            recipeSuggestions = recipeSuggestions,
            onCurrentMealEditingUpdate = onCurrentMealEditingUpdate,
            onAddMeal = onAddMeal,
            onRecipeClick = onRecipeClick,
            onRemoveMeal = onRemoveMeal,
            onAddNewRecipe = { onAddNewRecipe(it, MealType.LUNCH, date) }
        )
        MealRowItem(
            originalMealType = MealType.DINNER,
            mealIconPainter = painterResource(R.drawable.icon_ramen_noodle),
            date = date,
            currentMealEditing = currentMealEditing,
            recipes = map[MealType.DINNER] ?: emptyList(),
            recipeSuggestions = recipeSuggestions,
            onCurrentMealEditingUpdate = onCurrentMealEditingUpdate,
            onAddMeal = onAddMeal,
            onRemoveMeal = onRemoveMeal,
            onRecipeClick = onRecipeClick,
            onAddNewRecipe = { onAddNewRecipe(it, MealType.LUNCH, date) }
        )
    }
}

@Composable
private fun MealRowItem(
    originalMealType: MealType,
    mealIconPainter: Painter,
    date: LocalDate,
    currentMealEditing: Triple<LocalDate, MealType, String>?,
    recipes: List<Recipe>,
    recipeSuggestions: List<Recipe>,
    onCurrentMealEditingUpdate: (Triple<LocalDate, MealType, String>?) -> Unit,
    onAddMeal: (Recipe, MealType, LocalDate) -> Unit,
    onRemoveMeal: (Recipe, MealType, LocalDate) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onAddNewRecipe: (String) -> Unit
) {
    val mealDate = currentMealEditing?.first
    val mealType = currentMealEditing?.second
    val mealEditText = currentMealEditing?.third

    fun shouldUpdate(type: MealType?): Boolean {
        if (type == null || mealDate == null) return true
        return date == mealDate && type == mealType
    }
    MealRow(
        mealType = originalMealType.value,
        mealIconPainter = mealIconPainter,
        mealEditText = if (shouldUpdate(originalMealType)) mealEditText.orEmpty() else "",
        onMealEditTextUpdate = {
            if (shouldUpdate(originalMealType)) onCurrentMealEditingUpdate(
                Triple(date, originalMealType, it)
            )
        },
        recipes = recipes,
        recipeSuggestions = if (shouldUpdate(originalMealType)) recipeSuggestions else emptyList(),
        onRecipeClick = onRecipeClick,
        onRecipeSuggestionSelect = { onAddMeal(it, originalMealType, date) },
        onFocusChanged = {
            if (it) {
                onCurrentMealEditingUpdate(
                    Triple(date, originalMealType, "")
                )
            }
        },
        onRemoveMeal = { onRemoveMeal(it, originalMealType, date) },
        onAddNewRecipe = onAddNewRecipe
    )
}

@Composable
private fun MealRow(
    mealType: String,
    mealIconPainter: Painter,
    mealEditText: String,
    recipes: List<Recipe>,
    recipeSuggestions: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onRecipeSuggestionSelect: (Recipe) -> Unit,
    onMealEditTextUpdate: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onRemoveMeal: (Recipe) -> Unit,
    onAddNewRecipe: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = mealIconPainter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier
                    .width(80.dp),
                text = mealType,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            BasicTextField(
                modifier = Modifier
                    .onFocusChanged {
                        onFocusChanged(it.hasFocus)
                    }
                    .weight(1f)
                    .padding(end = 4.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                value = mealEditText,
                onValueChange = onMealEditTextUpdate,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    this@Row.AnimatedVisibility(
                        visible = mealEditText.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear_text),
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onMealEditTextUpdate("")
                                    focusManager.clearFocus()
                                }
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .alpha(if (mealEditText.isEmpty()) 1f else 0f)
                        .fillMaxSize(),
                    text = "Add meal",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelLarge
                )
                it()
            }
        }
        VerticalAnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = recipeSuggestions,
            label = "RecipeSuggestionAnimation",
        ) { suggestions ->
            val padding by animateDpAsState(if (suggestions.isNotEmpty() || mealEditText.length >= 2) 8.dp else 0.dp)
            RecipeSuggestion(
                modifier = Modifier.padding(start = padding, end = padding, bottom = padding),
                suggestions = suggestions,
                onSuggestionItemSelected = {
                    onRecipeSuggestionSelect(it)
                    focusManager.clearFocus()
                },
                recipeName = mealEditText,
                onRecipeAddClick = {
                    onAddNewRecipe(mealEditText)
                    focusManager.clearFocus()
                }
            )
        }
        VerticalAnimatedContent(
            targetState = recipes,
            label = "MealDayAnimation"
        ) { recipes ->
            Column {
                recipes.forEach { recipe ->
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .clickable { onRecipeClick(recipe) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RecipeItemImage(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                                    .clip(shape = MaterialTheme.shapes.extraSmall),
                                imagePath = recipe.imagePath
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = recipe.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = {
                                onRemoveMeal(recipe)
                                focusManager.clearFocus()
                            }) {
                            Icon(
                                modifier = Modifier.padding(2.dp),
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = stringResource(R.string.clear_text)
                            )
                        }
                    }
                }
            }
        }
        if (recipes.isNotEmpty()) Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun DateView(dateFrom: LocalDate, dateTo: LocalDate, onAutoPlanClick: () -> Unit = {}) {
    Row(modifier = Modifier.padding(top = 8.dp)) {
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = dateFrom,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { dateFrom ->
            Text(
                modifier = Modifier.weight(1f),
                text = "${dateFrom.format()} - ${dateTo.format()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }

        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onAutoPlanClick() }
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            text = "Auto Plan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
private fun WeekSelector(
    isSelectingWeek: Boolean,
    selectedWeekText: String,
    weeksAvailable: List<Pair<Int, String>>,
    onIsSelectingWeekUpdate: (Boolean) -> Unit,
    onSelectedWeekTextUpdate: (Int, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onIsSelectingWeekUpdate(true) }) {
        Box(modifier = Modifier.fillMaxWidth()) {
            this@Card.AnimatedVisibility(
                visible = !isSelectingWeek,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 100)),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null
                )
            }
            Column {
                weeksAvailable.forEachIndexed { index, (week, text) ->
                    AnimatedVisibility(
                        visible = selectedWeekText == text || isSelectingWeek,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        WeekText(
                            text = text,
                            onClick = {
                                if (isSelectingWeek) {
                                    onSelectedWeekTextUpdate(week, text)
                                } else {
                                    onIsSelectingWeekUpdate(true)
                                }
                            })
                    }
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = index < weeksAvailable.size - 1 && isSelectingWeek,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekText(text: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W600
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(state = HomeScreenState())
    }
}

private fun LocalDate.format(): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$dayOfWeek / $dayOfMonth $month"
}

private fun LocalDate.getDayName(): String {
    return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}