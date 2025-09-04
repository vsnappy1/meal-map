package com.randos.mealmap.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF3B6939)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFBCF0B4)
val onPrimaryContainerLight = Color(0xFF235024)
val secondaryLight = Color(0xFF8F4C37)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDBD0)
val onSecondaryContainerLight = Color(0xFF723522)
val tertiaryLight = Color(0xFF785A0B)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFDFA0)
val onTertiaryContainerLight = Color(0xFF5C4300)
val errorLight = Color(0xFF904A45)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF73332F)
val backgroundLight = Color(0xFFF7FBF1)
val onBackgroundLight = Color(0xFF191D17)
val surfaceLight = Color(0xFFF5FAFB)
val onSurfaceLight = Color(0xFF171D1E)
val surfaceVariantLight = Color(0xFFDBE4E6)
val onSurfaceVariantLight = Color(0xFF3F484A)
val outlineLight = Color(0xFF6F797A)
val outlineVariantLight = Color(0xFFBFC8CA)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2B3133)
val inverseOnSurfaceLight = Color(0xFFECF2F3)
val inversePrimaryLight = Color(0xFFA1D39A)
val surfaceDimLight = Color(0xFFD5DBDC)
val surfaceBrightLight = Color(0xFFF5FAFB)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFEFF5F6)
val surfaceContainerLight = Color(0xFFE9EFF0)
val surfaceContainerHighLight = Color(0xFFE3E9EA)
val surfaceContainerHighestLight = Color(0xFFDEE3E5)

val primaryDark = Color(0xFFA1D39A)
val onPrimaryDark = Color(0xFF0A390F)
val primaryContainerDark = Color(0xFF235024)
val onPrimaryContainerDark = Color(0xFFBCF0B4)
val secondaryDark = Color(0xFFFFB59F)
val onSecondaryDark = Color(0xFF561F0E)
val secondaryContainerDark = Color(0xFF723522)
val onSecondaryContainerDark = Color(0xFFFFDBD0)
val tertiaryDark = Color(0xFFEAC16C)
val onTertiaryDark = Color(0xFF402D00)
val tertiaryContainerDark = Color(0xFF5C4300)
val onTertiaryContainerDark = Color(0xFFFFDFA0)
val errorDark = Color(0xFFFFB3AC)
val onErrorDark = Color(0xFF571E1A)
val errorContainerDark = Color(0xFF73332F)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF10140F)
val onBackgroundDark = Color(0xFFE0E4DB)
val surfaceDark = Color(0xFF0E1415)
val onSurfaceDark = Color(0xFFDEE3E5)
val surfaceVariantDark = Color(0xFF3F484A)
val onSurfaceVariantDark = Color(0xFFBFC8CA)
val outlineDark = Color(0xFF899294)
val outlineVariantDark = Color(0xFF3F484A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFDEE3E5)
val inverseOnSurfaceDark = Color(0xFF2B3133)
val inversePrimaryDark = Color(0xFF3B6939)
val surfaceDimDark = Color(0xFF0E1415)
val surfaceBrightDark = Color(0xFF343A3B)
val surfaceContainerLowestDark = Color(0xFF090F10)
val surfaceContainerLowDark = Color(0xFF171D1E)
val surfaceContainerDark = Color(0xFF1B2122)
val surfaceContainerHighDark = Color(0xFF252B2C)
val surfaceContainerHighestDark = Color(0xFF303637)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    context: Context
): ColorScheme {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    return colorScheme
}
