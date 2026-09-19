package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MmaPurple,
    onPrimary = Color.White,
    primaryContainer = MmaDarkCardElevated,
    onPrimaryContainer = Color.White,
    secondary = MmaPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1238),
    onSecondaryContainer = Color(0xFFFFD8E4),
    tertiary = MmaCyan,
    onTertiary = Color.Black,
    background = MmaDarkNavy,
    onBackground = MmaTextPrimaryDark,
    surface = MmaDarkCard,
    onSurface = MmaTextPrimaryDark,
    surfaceVariant = MmaDarkCardElevated,
    onSurfaceVariant = MmaTextSecondaryDark,
    outline = MmaDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MmaPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = MmaPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = MmaBlue,
    onTertiary = Color.White,
    background = MmaLightBg,
    onBackground = MmaTextPrimaryLight,
    surface = MmaLightCard,
    onSurface = MmaTextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = MmaTextSecondaryLight,
    outline = MmaLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

