package com.randos.data.manager

import android.content.SharedPreferences
import com.randos.domain.manager.SettingsManager
import com.randos.domain.type.Day
import androidx.core.content.edit

internal class SettingsManagerImpl(private val sharedPreferences: SharedPreferences) : SettingsManager {

    companion object {
        private const val FIRST_DAY_OF_WEEK = "first_day_of_week"
    }

    override fun setFirstDayOfTheWeek(day: Day) {
        sharedPreferences.edit { putString(FIRST_DAY_OF_WEEK, day.name) }
    }

    override fun getFirstDayOfTheWeek(): Day {
        val name = sharedPreferences.getString(FIRST_DAY_OF_WEEK, Day.MONDAY.name)
        return name?.let { Day.valueOf(it) } ?: Day.MONDAY
    }

    override fun getDaysOfWeek(): List<Day> {
        return listOf(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )
    }
}
