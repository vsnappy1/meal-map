package com.randos.data.database.util

import com.randos.domain.type.RecipeTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RecipeTagListConverterTest {

    private lateinit var recipeTagListConverter: RecipeTagListConverter

    @Before
    fun setUp() {
        recipeTagListConverter = RecipeTagListConverter()
    }

    @Test
    fun fromString_should_convert_string_to_list() {
        // Given
        val input = "[\"BREAKFAST\",\"CHICKEN\"]"

        // When
        val result = recipeTagListConverter.fromString(input)

        // Then
        assertEquals(listOf(RecipeTag.BREAKFAST, RecipeTag.CHICKEN), result)
    }

    @Test
    fun fromList_should_convert_list_to_string() {
        // Given
        val input = listOf(RecipeTag.BREAKFAST, RecipeTag.CHICKEN)

        // When
        val result = recipeTagListConverter.fromList(input)

        // Then
        assertEquals("[\"BREAKFAST\",\"CHICKEN\"]", result)
    }

    @Test
    fun fromString_when_input_is_null_should_return_null() {
        // When
        val result = recipeTagListConverter.fromString(null)

        // Then
        assertNull(result)
    }

    @Test
    fun fromList_when_input_is_null_should_return_null() {
        // When
        val result = recipeTagListConverter.fromList(null)

        // Then
        assertNull(result)
    }

}