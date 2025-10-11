package com.randos.data.manager

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.randos.data.manager.SettingsManagerImpl.Companion.FIRST_DAY_OF_WEEK
import com.randos.data.manager.SettingsManagerImpl.Companion.IS_FIRST_TIME_USING_APP
import com.randos.domain.manager.SettingsManager
import com.randos.domain.type.Day
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsManagerImplTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsManager: SettingsManager

    @Before
    fun setUp() {
        val context = getApplicationContext<Application>()
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        settingsManager = SettingsManagerImpl(sharedPreferences)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun setFirstDayOfTheWeek_saves_correct_value() {
        // Given
        val day = Day.WEDNESDAY

        // When
        settingsManager.setFirstDayOfTheWeek(day)

        // Then
        val savedDay = sharedPreferences.getString(FIRST_DAY_OF_WEEK, "")
        assertEquals(day.name, savedDay)
    }

    @Test
    fun getFirstDayOfTheWeek_returns_correct_value() {
        // Given
        val day = Day.WEDNESDAY
        settingsManager.setFirstDayOfTheWeek(day)

        // When
        val result = settingsManager.getFirstDayOfTheWeek()

        // Then
        assertEquals(day, result)
    }

    @Test
    fun getFirstDayOfTheWeek_when_not_set_should_return_monday_by_default() {
        // When
        val result = settingsManager.getFirstDayOfTheWeek()

        // Then
        assertEquals(Day.MONDAY, result)
    }

    @Test
    fun getDaysOfWeek_returns_correct_list() {
        // Given
        val expected = listOf(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )

        // When
        val result = settingsManager.getDaysOfWeek()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun isFirstTimeUsingApp_returns_correct_value() {
        // Given
        val expected = true

        // When
        val result = settingsManager.isFirstTimeUsingApp()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun setFirstTimeUsingApp_saves_correct_value() {
        // Given
        val isFirstTime = false

        // When
        settingsManager.setFirstTimeUsingApp(isFirstTime)

        // Then
        val savedValue = sharedPreferences.getBoolean(IS_FIRST_TIME_USING_APP, true)
        assertEquals(isFirstTime, savedValue)
    }

}