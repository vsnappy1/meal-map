package com.randos.mealmap.utils

import com.randos.domain.type.Day
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

object CalendarUtils {
    fun formatTime(minutes: Int?): String {
        if (minutes == null) return "--"
        if (minutes <= 0) return "0 min"

        val hours = minutes / 60
        val mins = minutes % 60

        return buildString {
            if (hours > 0) {
                append(hours)
                append(" hr")
                if (hours > 1) append("s") // plural
            }
            if (hours > 0 && mins > 0) append(" ")
            if (mins > 0) {
                append(mins)
                append(" min")
                if (mins > 1) append("s") // plural
            }
        }
    }

    fun getWeekStartAndEnd(week: Int, firstDayOfTheWeek: DayOfWeek): Pair<LocalDate, LocalDate> {
        val week = LocalDate.now().plusWeeks(week.toLong())
        val weekStartDate = week.with(TemporalAdjusters.previous(firstDayOfTheWeek))
        val weekEndDate = weekStartDate.plusDays(6)
        return Pair(weekStartDate, weekEndDate)
    }
}

fun LocalDate.format(): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$dayOfWeek / $dayOfMonth $month"
}

fun LocalDate.getDayName(): String = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

fun Day.toDayOfWeek(): DayOfWeek = when (this) {
    Day.MONDAY -> DayOfWeek.MONDAY
    Day.TUESDAY -> DayOfWeek.TUESDAY
    Day.WEDNESDAY -> DayOfWeek.WEDNESDAY
    Day.THURSDAY -> DayOfWeek.THURSDAY
    Day.FRIDAY -> DayOfWeek.FRIDAY
    Day.SATURDAY -> DayOfWeek.SATURDAY
    Day.SUNDAY -> DayOfWeek.SUNDAY
}
