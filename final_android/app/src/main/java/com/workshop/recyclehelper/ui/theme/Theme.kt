package com.workshop.recyclehelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Color schemes for the app
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),      // Green
    secondary = Color(0xFF2196F3),     // Blue
    tertiary = Color(0xFFFF9800),      // Orange
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),      // Green
    secondary = Color(0xFF2196F3),     // Blue
    tertiary = Color(0xFFFF9800),      // Orange
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
)

/**
 * RecycleHelperTheme - App theme
 *
 * Applies Material 3 theming to the app
 */
@Composable
fun RecycleHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
