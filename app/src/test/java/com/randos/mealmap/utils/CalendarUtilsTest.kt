package com.randos.mealmap.utils

import com.randos.domain.type.Day
import com.randos.mealmap.utils.CalendarUtils.formatTime
import com.randos.mealmap.utils.CalendarUtils.getWeekStartAndEnd
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarUtilsTest {

    private val date = LocalDate.of(2025, 10, 9)

    @Before
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns date
    }

    @Test
    fun `formatTime with null input`() {
        // When
        val result = formatTime(null)

        // Then
        assertEquals("--", result)
    }

    @Test
    fun `formatTime with zero minutes`() {
        // When
        val result = formatTime(0)

        // Then
        assertEquals("0 min", result)
    }

    @Test
    fun `formatTime with negative minutes`() {
        // When
        val result = formatTime(-6)

        // Then
        assertEquals("0 min", result)
    }

    @Test
    fun `formatTime with single minute`() {
        // When
        val result = formatTime(1)

        // Then
        assertEquals("1 min", result)
    }

    @Test
    fun `formatTime with multiple minutes less than an hour`() {
        // When
        val result = formatTime(40)

        // Then
        assertEquals("40 mins", result)
    }

    @Test
    fun `formatTime with exactly one hour`() {
        // When
        val result = formatTime(60)

        // Then
        assertEquals("1 hr", result)
    }

    @Test
    fun `formatTime with one hour and single minute`() {
        // When
        val result = formatTime(61)

        // Then
        assertEquals("1 hr 1 min", result)
    }

    @Test
    fun `formatTime with one hour and multiple minutes`() {
        // When
        val result = formatTime(65)

        // Then
        assertEquals("1 hr 5 mins", result)
    }

    @Test
    fun `formatTime with multiple hours`() {
        // When
        val result = formatTime(120)

        // Then
        assertEquals("2 hrs", result)
    }

    @Test
    fun `formatTime with multiple hours and single minute`() {
        // When
        val result = formatTime(121)

        // Then
        assertEquals("2 hrs 1 min", result)

    }

    @Test
    fun `formatTime with multiple hours and multiple minutes`() {
        // When
        val result = formatTime(125)

        // Then
        assertEquals("2 hrs 5 mins", result)
    }

    @Test
    fun `getWeekStartAndEnd for current week`() {
        // When
        val (startDate, endDate) = getWeekStartAndEnd(0, DayOfWeek.MONDAY)

        // Then
        assertEquals(LocalDate.of(2025, 10, 6), startDate)
        assertEquals(LocalDate.of(2025, 10, 12), endDate)
    }

    @Test
    fun `getWeekStartAndEnd for next week`() {
        // When
        val (startDate, endDate) = getWeekStartAndEnd(1, DayOfWeek.MONDAY)

        // Then
        assertEquals(LocalDate.of(2025, 10, 13), startDate)
        assertEquals(LocalDate.of(2025, 10, 19), endDate)
    }

    @Test
    fun `getWeekStartAndEnd for previous week`() {
        // When
        val (startDate, endDate) = getWeekStartAndEnd(-1, DayOfWeek.MONDAY)

        // Then
        assertEquals(LocalDate.of(2025, 9, 29), startDate)
        assertEquals(LocalDate.of(2025, 10, 5), endDate)
    }

    @Test
    fun `getWeekStartAndEnd interval check`() {
        // When
        val (startDate, endDate) = getWeekStartAndEnd(0, DayOfWeek.MONDAY)

        // Then
        assertEquals(6, endDate.dayOfYear - startDate.dayOfYear)
    }

    @Test
    fun `getWeekStartAndEnd start day of week check`() {
        // When
        val (startDate, _) = getWeekStartAndEnd(0, DayOfWeek.MONDAY)

        // Then
        assertEquals(DayOfWeek.MONDAY, startDate.dayOfWeek)
    }

    @Test
    fun `getWeekStartAndEnd across year boundary  end of year `() {
        every { LocalDate.now() } returns LocalDate.of(2025, 12, 31)
        val (startDate, endDate) = getWeekStartAndEnd(0, DayOfWeek.MONDAY)

        // Then
        assertEquals(LocalDate.of(2025, 12, 29), startDate)
        assertEquals(LocalDate.of(2026, 1, 4), endDate)
    }

    @Test
    fun `getWeekStartAndEnd during a leap year`() {
        // Given
        every { LocalDate.now() } returns LocalDate.of(2024, 2, 29)

        // When
        val (startDate, endDate) = getWeekStartAndEnd(0, DayOfWeek.MONDAY)

        // Then
        assertEquals(LocalDate.of(2024, 2, 26), startDate)
        assertEquals(LocalDate.of(2024, 3, 3), endDate)
    }

    @Test
    fun `format on LocalDate returns a formatted string`(){
        // When
        val result = date.format()

        // Then
        assertEquals("Thu / 9 Oct", result)
    }

    @Test
    fun `getDayName on LocalDate returns name of the day`(){
        // When
        val result = date.getDayName()

        // Then
        assertEquals("Thursday", result)
    }

    @Test
    fun `toDayOfWeek on Day returns DayOfWeek`(){
        Day.entries.forEach {
            assertEquals(DayOfWeek.valueOf(it.name), it.toDayOfWeek())
        }
    }
}