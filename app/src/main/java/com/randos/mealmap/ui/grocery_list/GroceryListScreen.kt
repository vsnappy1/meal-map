package com.randos.mealmap.ui.grocery_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        onSelectedWeekTextUpdate = viewModel::onSelectedWeekTextUpdate
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
            targetState = state.groceryIngredients,
            label = "GroceryListAnimation"
        ) { groceryIngredients ->
            LazyColumn {
                items(groceryIngredients) { ingredient ->
                    GroceryListItem(groceryIngredient = ingredient)
                }
            }
        }
    }
}

@Composable
fun GroceryListItem(groceryIngredient: GroceryIngredient) {
    var isChecked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            modifier = Modifier.size(32.dp),
            checked = isChecked,
            onCheckedChange = { isChecked = !isChecked })
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
            text = groceryIngredient.name,
            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W400
        )
        Text(
            text = getIngredientAmountsByUnitText(
                groceryIngredient.amountsByUnit,
                stringResource(R.string.ingredient_default_unit)
            ),
            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None),
            color = MaterialTheme.colorScheme.onSurface
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

@Preview
@Composable
private fun PreviewGroceryListScreen() {
    GroceryListScreen(GroceryListScreenState())
}