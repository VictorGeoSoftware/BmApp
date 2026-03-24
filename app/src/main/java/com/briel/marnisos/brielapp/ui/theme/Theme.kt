package com.briel.marnisos.brielapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class BrielExtendedColors(
    val tableHeaderBackground: Color,
    val tableHeaderContent: Color,
    val tableBorder: Color,
    val tableText: Color,
    val tableTextSecondary: Color,
    val headerHighlight: Color,
    val headerBackground: Color,
    val sectionHighlight: Color,
    val sectionBorder: Color
)

private val LightExtendedColors = BrielExtendedColors(
    tableHeaderBackground = LightTableHeaderBackground,
    tableHeaderContent = LightTableHeaderContent,
    tableBorder = LightTableBorder,
    tableText = LightTableText,
    tableTextSecondary = LightTableTextSecondary,
    headerHighlight = LightHeaderHighlight,
    headerBackground = LightHeaderBackground,
    sectionHighlight = LightSectionHighlight,
    sectionBorder = LightSectionBorder
)

private val DarkExtendedColors = BrielExtendedColors(
    tableHeaderBackground = DarkTableHeaderBackground,
    tableHeaderContent = DarkTableHeaderContent,
    tableBorder = DarkTableBorder,
    tableText = DarkTableText,
    tableTextSecondary = DarkTableTextSecondary,
    headerHighlight = DarkHeaderHighlight,
    headerBackground = DarkHeaderBackground,
    sectionHighlight = DarkSectionHighlight,
    sectionBorder = DarkSectionBorder
)

private val LocalBrielExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val extendedColors: BrielExtendedColors
    @Composable
    get() = LocalBrielExtendedColors.current

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    secondary = AppSecondary,
    onSecondary = AppOnSecondary,
    background = Color(0xFF020617),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFE2E8F0),
    outline = Color(0xFF94A3B8)
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    secondary = AppSecondary,
    onSecondary = AppOnSecondary,
    background = AppBackground,
    onBackground = AppOnBackground,
    surface = AppSurface,
    onSurface = AppOnSurface,
    outline = AppOutline
)

@Composable
fun BrielAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalBrielExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}