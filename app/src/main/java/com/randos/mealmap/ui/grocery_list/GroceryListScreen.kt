package com.randos.mealmap.ui.grocery_list

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.randos.domain.model.GroceryIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.R
import com.randos.mealmap.ui.components.DateView
import com.randos.mealmap.ui.components.ScreenHeadingText
import com.randos.mealmap.ui.components.VerticalAnimatedContent
import com.randos.mealmap.ui.components.WeekSelector
import com.randos.mealmap.utils.Utils.formatQuantity


@Composable
fun GroceryListScreen(
    viewModel: GroceryListScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.observeAsState(GroceryListScreenState())
    GroceryListScreen(
        state = state,
        onIsSelectingWeekUpdate = viewModel::onIsSelectingWeekUpdate,
        onSelectedWeekTextUpdate = viewModel::onSelectedWeekTextUpdate,
        onIngredientCheckedUpdate = viewModel::onIngredientCheckedUpdate
    )
    LaunchedEffect(Unit) {
        viewModel.getGroceryIngredients()
    }
}

@Composable
private fun GroceryListScreen(
    state: GroceryListScreenState = GroceryListScreenState(),
    onIsSelectingWeekUpdate: (Boolean) -> Unit = {},
    onSelectedWeekTextUpdate: (Pair<Int, String>) -> Unit = { },
    onIngredientCheckedUpdate: (Int, Boolean) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ScreenHeadingText(text = "Grocery List")
        WeekSelector(
            isSelectingWeek = state.isSelectingWeek,
            selectedWeek = state.selectedWeek.second,
            onIsSelectingWeekUpdate = onIsSelectingWeekUpdate,
            onSelectedWeekUpdate = { week, text ->
                onSelectedWeekTextUpdate(Pair(week, text))
            }
        )
        AnimatedVisibility(visible = state.isSelectingWeek) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        DateView(
            modifier = Modifier.fillMaxWidth(),
            dateFrom = state.dateFrom,
            dateTo = state.dateTo
        )
        VerticalAnimatedContent(
            targetState = state.groceryIngredients.map { it.name },
            label = "GroceryListAnimation"
        ) { groceryIngredients ->
            LazyColumn {
                itemsIndexed(
                    items = state.groceryIngredients,
                    key = { _, ingredient -> ingredient.name }) { index, ingredient ->
                    GroceryListItem(
                        groceryIngredient = ingredient,
                        onCheckedChange = { checked -> onIngredientCheckedUpdate(index, checked) })
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = state.groceryIngredients.isEmpty()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 64.dp),
                    text = "Your grocery list is empty, \n Plan your meals to see what you need!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

@Composable
fun GroceryListItem(groceryIngredient: GroceryIngredient, onCheckedChange: (Boolean) -> Unit) {
    val isChecked = groceryIngredient.isChecked
    val textStyle =
        MaterialTheme.typography.bodyLarge.copy(textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            modifier = Modifier.size(32.dp),
            checked = isChecked,
            onCheckedChange = { onCheckedChange(!isChecked) })
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
            text = groceryIngredient.name,
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W400
        )
        Text(
            text = getIngredientAmountsByUnitText(
                groceryIngredient.amountsByUnit,
                stringResource(R.string.ingredient_default_unit)
            ),
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

private fun getIngredientAmountsByUnitText(
    amountsByUnit: List<Pair<IngredientUnit?, Double>>,
    defaultUnit: String
): String {
    val stringBuilder = StringBuilder()
    amountsByUnit.forEach { (unit, quantity) ->
        val quantityText = formatQuantity(quantity)
        val unitText = unit?.value ?: defaultUnit
        stringBuilder.append("$quantityText $unitText\n")
    }
    return stringBuilder.toString().trim()
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewGroceryListScreen() {
    GroceryListScreen(GroceryListScreenState())
}