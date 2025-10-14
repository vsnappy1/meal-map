package com.randos.mealmap.utils

import com.randos.mealmap.utils.NumberUtils.formatIngredientQuantity
import org.junit.Assert.*
import org.junit.Test

class NumberUtilsTest {

    @Test
    fun `Test with zero quantity`() {
        // When
        val result = formatIngredientQuantity(0.0)

        // Then
        assertEquals("0", result)
    }

    @Test
    fun `Test with a simple integer quantity`() {
        // When
        val result = formatIngredientQuantity(5.0)

        // Then
        assertEquals("5", result)
    }

    @Test
    fun `Test with a pure 1 4 fraction`() {
        // When
        val result = formatIngredientQuantity(0.25)

        // Then
        assertEquals("¼", result)
    }

    @Test
    fun `Test with a pure 1 3 fraction`() {
        // When
        val result = formatIngredientQuantity(0.33)

        // Then
        assertEquals("⅓", result)

    }

    @Test
    fun `Test with a pure 1 2 fraction`() {
        // When
        val result = formatIngredientQuantity(0.5)

        // Then
        assertEquals("½", result)
    }

    @Test
    fun `Test with a pure 2 3 fraction`() {
        // When
        val result = formatIngredientQuantity(0.66)

        // Then
        assertEquals("⅔", result)
    }

    @Test
    fun `Test with a pure 3 4 fraction`() {
        // When
        val result = formatIngredientQuantity(0.75)

        // Then
        assertEquals("¾", result)
    }

    @Test
    fun `Test with a whole number and a 1 4 fraction`() {
        // When
        val result = formatIngredientQuantity(2.25)

        // Then
        assertEquals("2¼", result)
    }

    @Test
    fun `Test with a whole number and a 1 2 fraction`() {
        // When
        val result = formatIngredientQuantity(3.5)

        // Then
        assertEquals("3½", result)
    }

    @Test
    fun `Test rounding up to a known fraction`() {
        // When
        val result = formatIngredientQuantity(0.26)

        // Then
        assertEquals("¼", result)
    }

    @Test
    fun `Test rounding down to a known fraction`() {
        // When
        val result = formatIngredientQuantity(0.74)

        // Then
        assertEquals("¾", result)
    }

    @Test
    fun `Test rounding with a whole number`() {
        // When
        val result = formatIngredientQuantity(4.33)

        // Then
        assertEquals("4⅓", result)
    }

    @Test
    fun `Test fallback formatting for one decimal place`() {
        // When
        val result = formatIngredientQuantity(1.1)

        // Then
        assertEquals("1.1", result)
    }

    @Test
    fun `Test fallback formatting for two decimal places`() {
        // When
        val result = formatIngredientQuantity(1.12)

        // Then
        assertEquals("1.12", result)
    }

    @Test
    fun `Test fallback formatting with rounding`() {
        // When
        val result = formatIngredientQuantity(1.888)

        // Then
        assertEquals("1.89", result)
    }

    @Test
    fun `Test with a large integer value`() {
        // When
        val result = formatIngredientQuantity(1000.0)

        // Then
        assertEquals("1000", result)
    }

    @Test
    fun `Test with a large number and fraction`() {
        // When
        val result = formatIngredientQuantity(100.25)

        // Then
        assertEquals("100¼", result)
    }

    @Test
    fun `Test with a large decimal number`() {
        // When
        val result = formatIngredientQuantity(99.99)

        // Then
        assertEquals("99.99", result)
    }

    @Test
    fun `Test a value halfway between two fractions`() {
        // When
        val result = formatIngredientQuantity(0.29)

        // Then
        assertEquals("¼", result)
    }

    @Test
    fun `Test very small decimal not near a fraction`() {
        // When
        val result = formatIngredientQuantity(0.01)

        // Then
        assertEquals("0.01", result)
    }
}