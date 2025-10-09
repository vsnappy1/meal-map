package com.randos.mealmap.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.randos.domain.type.Day
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Permissions should be called in an Activity context")
}

fun LocalDate.format(): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$dayOfWeek / $dayOfMonth $month"
}

fun LocalDate.getDayName(): String {
    return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}

fun Modifier.defaultMainContainerPadding(): Modifier {
    return this.padding(start = 16.dp, end = 16.dp, top = 16.dp)
}

fun Day.toDayOfWeek(): DayOfWeek {
    return when (this) {
        Day.MONDAY -> DayOfWeek.MONDAY
        Day.TUESDAY -> DayOfWeek.TUESDAY
        Day.WEDNESDAY -> DayOfWeek.WEDNESDAY
        Day.THURSDAY -> DayOfWeek.THURSDAY
        Day.FRIDAY -> DayOfWeek.FRIDAY
        Day.SATURDAY -> DayOfWeek.SATURDAY
        Day.SUNDAY -> DayOfWeek.SUNDAY
    }
}
