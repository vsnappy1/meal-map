package com.randos.data.database.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

class DateConverterTest {

    private lateinit var converter: DateConverter

    @Before
    fun setUp() {
        converter = DateConverter()
    }

    @Test
    fun fromDate_should_convert_date_to_long() {
        // Given
        val input = Date(1624137600000)
        val expected = 1624137600000

        // When
        val result = converter.fromDate(input)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun fromDate_when_input_is_null_should_return_null() {
        // Given
        val input = null

        // When
        val result = converter.fromDate(input)

        // Then
        assertNull(result)
    }

    @Test
    fun fromLong_should_convert_long_to_date() {
        // Given
        val input = 1624137600000
        val expected = Date(1624137600000)

        // When
        val result = converter.fromLong(input)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun fromLong_when_input_is_null_should_return_null() {
        // Given
        val input = null

        // When
        val result = converter.fromLong(input)

        // Then
        assertNull(result)
    }

}