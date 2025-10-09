package com.randos.data.database.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DateConverterTest {

    private lateinit var converter: LocalDateConverter

    @Before
    fun setUp() {
        converter = LocalDateConverter()
    }

    @Test
    fun fromDate_should_convert_date_to_long() {
        // Given
        val input = LocalDate.now()
        val expected = input.toString()

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
        val date = LocalDate.now()
        val input = date.toString()
        val expected = LocalDate.parse(date.toString())

        // When
        val result = converter.fromString(input)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun fromLong_when_input_is_null_should_return_null() {
        // Given
        val input = null

        // When
        val result = converter.fromString(input)

        // Then
        assertNull(result)
    }

}