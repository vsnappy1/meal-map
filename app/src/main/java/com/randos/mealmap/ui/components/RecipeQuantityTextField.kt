package com.randos.mealmap.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Ingredient
import com.randos.domain.model.RecipeIngredient
import com.randos.domain.type.IngredientUnit
import com.randos.mealmap.utils.Constants.RECIPE_INGREDIENT_QUANTITY_MAX_LENGTH

@Composable
fun RecipeQuantityTextField(
    modifier: Modifier = Modifier,
    ingredient: RecipeIngredient,
    onUpdateQuantity: (Double) -> Unit,
    onKeyboardDone: () -> Unit
) {
    val quantityString = if (ingredient.quantity % 1.0 == 0.0) {
        ingredient.quantity.toInt().toString()
    } else {
        ingredient.quantity.toString()
    }
    var quality by remember { mutableStateOf(quantityString) }

    BasicTextField(
        modifier = modifier
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
            onUpdateQuantity(quality.toDoubleOrNull() ?: 0.0)
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

@Preview
@Composable
fun RecipeQuantityTextFieldPreview() {
    RecipeQuantityTextField(
        ingredient = RecipeIngredient(
            ingredient = Ingredient(name = "ingredient"),
            quantity = 2.0,
            unit = IngredientUnit.GRAM
        ),
        onUpdateQuantity = {},
        onKeyboardDone = {}
    )
}
