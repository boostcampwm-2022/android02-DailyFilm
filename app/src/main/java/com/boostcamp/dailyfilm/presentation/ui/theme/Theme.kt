package com.boostcamp.dailyfilm.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.example.composeapplication.ui.theme.Shapes
import com.example.composeapplication.ui.theme.Typography
import com.example.composeapplication.ui.theme.darkOnPrimary
import com.example.composeapplication.ui.theme.darkOnSurface
import com.example.composeapplication.ui.theme.darkPrimary
import com.example.composeapplication.ui.theme.darkSurface
import com.example.composeapplication.ui.theme.onPrimary
import com.example.composeapplication.ui.theme.onSurface
import com.example.composeapplication.ui.theme.primary
import com.example.composeapplication.ui.theme.surface

private val DarkColorPalette = darkColors(
    surface = darkSurface,
    onSurface = darkOnSurface,
    primary = darkPrimary,
    onPrimary = darkOnPrimary
)

private val LightColorPalette = lightColors(
    surface = surface,
    onSurface = onSurface,
    primary = primary,
    onPrimary = onPrimary
)

@Composable
fun ComposeApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}