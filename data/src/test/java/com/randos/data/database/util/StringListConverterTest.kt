package com.randos.data.database.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

internal class StringListConverterTest {

    private lateinit var converter: StringListConverter

    @Before
    fun setUp() {
        converter = StringListConverter()
    }

    @Test
    fun fromString_should_convert_string_to_list() {
        // Given
        val input = "[\"apple\",\"banana\",\"cherry\"]"
        val expected = listOf("apple", "banana", "cherry")

        // When
        val result = converter.fromString(input)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun fromString_when_input_is_null_should_return_null() {
        // Given
        val input = null

        // When
        val result = converter.fromString(input)

        // Then
        assertNull(result)
    }

    @Test
    fun fromList_should_convert_list_to_string() {
        // Given
        val input = listOf("apple", "banana", "cherry")
        val expected = "[\"apple\",\"banana\",\"cherry\"]"

        // When
        val result = converter.fromList(input)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun fromList_when_input_is_null_should_return_null() {
        // Given
        val input = null

        // When
        val result = converter.fromList(input)

        // Then
        assertNull(result)
    }
}
