package com.randos.domain.manager

import com.randos.domain.type.Day

interface SettingsManager {
    fun setFirstDayOfTheWeek(day: Day)
    fun getFirstDayOfTheWeek(): Day
    fun getDaysOfWeek(): List<Day>
    fun isFirstTimeUsingApp(): Boolean
    fun setFirstTimeUsingApp(value: Boolean)
}