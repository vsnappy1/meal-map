package com.randos.data.manager

import android.content.SharedPreferences
import com.randos.domain.manager.SettingsManager
import com.randos.domain.type.Day
import androidx.core.content.edit
import jakarta.inject.Inject

internal class SettingsManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) :
    SettingsManager {

    companion object {
        const val FIRST_DAY_OF_WEEK = "first_day_of_week"
        const val IS_FIRST_TIME_USING_APP = "is_first_time_using_app"
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

    override fun isFirstTimeUsingApp(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_USING_APP, true)
    }

    override fun setFirstTimeUsingApp(value: Boolean) {
        sharedPreferences.edit { putBoolean(IS_FIRST_TIME_USING_APP, value) }
    }
}
