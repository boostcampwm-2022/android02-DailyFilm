package com.boostcamp.dailyfilm.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.composeapplication.ui.theme.Shapes
import com.example.composeapplication.ui.theme.Typography
import com.example.composeapplication.ui.theme.black
import com.example.composeapplication.ui.theme.darkOnPrimary
import com.example.composeapplication.ui.theme.darkOnSurface
import com.example.composeapplication.ui.theme.darkPrimary
import com.example.composeapplication.ui.theme.darkSurface
import com.example.composeapplication.ui.theme.lightBlack
import com.example.composeapplication.ui.theme.onPrimary
import com.example.composeapplication.ui.theme.onSurface
import com.example.composeapplication.ui.theme.primary
import com.example.composeapplication.ui.theme.surface
import com.example.composeapplication.ui.theme.white

private val DarkColorPalette = darkColors(
    surface = lightBlack,
    onSurface = white,
    primary = black,
    onPrimary = black,
    background = lightBlack,
    onBackground = white
)

private val LightColorPalette = lightColors(
    surface = white,
    onSurface = black,
    primary = Color(0xFFF0F0F0),
    onPrimary = white,
    onBackground = black,
    background = white
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