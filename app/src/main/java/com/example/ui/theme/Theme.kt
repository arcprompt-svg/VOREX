package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ElegantDarkColorScheme = darkColorScheme(
    primary = ElegantPrimary,
    onPrimary = ElegantOnPrimary,
    secondary = ElegantSecondaryContainer,
    onSecondary = ElegantOnSecondaryContainer,
    tertiary = SignalGreen,
    error = SignalRed,
    background = ElegantBackground,
    surface = ElegantSurface,
    surfaceVariant = ElegantBorder,
    onBackground = ElegantOnBackground,
    onSurface = ElegantOnSurface,
    onSurfaceVariant = ElegantOnBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode for futuristic Elegant VoreX Console
    dynamicColor: Boolean = false, // Disable to preserve custom Elegant Dark style
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElegantDarkColorScheme,
        typography = Typography,
        content = content
    )
}
