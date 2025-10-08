package com.randos.mealmap.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
