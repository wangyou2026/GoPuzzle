package com.gopuzzle.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = WoodBrownLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = WoodBrownDark,
    secondary = BambooGreenLight,
    onSecondary = OnPrimaryLight,
    secondaryContainer = BambooGreenDark,
    background = DarkBackground,
    onBackground = OnBackgroundDark,
    surface = DarkBackground,
    onSurface = OnBackgroundDark,
    error = IncorrectRed,
    onError = OnPrimaryLight
)

private val LightColorScheme = lightColorScheme(
    primary = WoodBrown,
    onPrimary = OnPrimaryLight,
    primaryContainer = WoodBrownLight,
    secondary = BambooGreen,
    onSecondary = OnPrimaryLight,
    secondaryContainer = BambooGreenLight,
    background = LightBackground,
    onBackground = OnBackgroundLight,
    surface = LightBackground,
    onSurface = OnBackgroundLight,
    error = IncorrectRed,
    onError = OnPrimaryLight
)

@Composable
fun GoPuzzleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
