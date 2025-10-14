package com.randos.mealmap.utils

import com.randos.domain.model.Recipe
import com.randos.mealmap.utils.Utils.recipeToShareableText
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate

class UtilsTest {

    val recipe = Constants.recipe

    @Test
    fun `Test with a complete recipe object`() {
        // When
        val result = recipeToShareableText(recipe)

        // Then
        assertEquals(
            "\uD83C\uDF7D Recipe Title\n" +
                    "\n" +
                    "Recipe Description\n" +
                    "\n" +
                    "⏱ Prep Time: 10 mins\n" +
                    "\uD83C\uDF73 Cook Time: 6 hrs 40 mins\n" +
                    "\uD83D\uDC65 Servings: 2\n" +
                    "\uD83C\uDFF7 Tag: Chicken\n" +
                    "⚖\uFE0F Heaviness: MEDIUM\n" +
                    "\uD83D\uDD25 Calories: 100 kcal\n" +
                    "\n" +
                    "\uD83D\uDED2 Ingredients:\n" +
                    "- 1 gm Ingredient 1\n" +
                    "- 2½ cup Ingredient 2\n" +
                    "- ¼ piece Ingredient 3\n" +
                    "\n" +
                    "\uD83D\uDC69\u200D\uD83C\uDF73 Instructions:\n" +
                    "1. Instruction 1\n" +
                    "2. Instruction 2\n" +
                    "3. Instruction 3", result
        )
    }

    @Test
    fun `Test with a minimal recipe object only title and empty lists `() {
        // When
        val result = recipeToShareableText(Recipe(title = "Recipe Title", dateCreated = LocalDate.now()))

        // Then
        assertEquals("\uD83C\uDF7D Recipe Title\n" +
                "\n" +
                "\uD83C\uDFF7 Tag: \n" +
                "\n" +
                "\uD83D\uDED2 Ingredients:\n" +
                "\n" +
                "\uD83D\uDC69\u200D\uD83C\uDF73 Instructions:", result
        )
    }
}