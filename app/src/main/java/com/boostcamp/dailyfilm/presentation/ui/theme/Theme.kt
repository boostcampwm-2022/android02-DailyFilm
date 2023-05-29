package com.boostcamp.dailyfilm.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorPalette = lightColors(
    primary = primary,
    primaryVariant = primaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError,
)

private val DarkColorPalette = darkColors(
    primary = darkPrimary,
    primaryVariant = darkPrimaryVariant,
    background = darkBackground,
    surface = darkSurface,
    error = darkError,
    onPrimary = darkOnPrimary,
    onBackground = darkOnBackground,
    onSurface = darkOnSurface,
    onError = darkOnError,
)

@Composable
fun DailyFilmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primaryVariant.toArgb()
            window.navigationBarColor = colors.primaryVariant.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme.not()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = darkTheme.not()
        }
    }

    MaterialTheme(
        colors = colors,
        content = content,
    )
}
