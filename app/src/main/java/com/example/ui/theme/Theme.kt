package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BdGreenPrimary,
    onPrimary = Color.White,
    secondary = BdGoldAccent,
    onSecondary = Color.Black,
    tertiary = BdRedAccent,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = OnDarkSurface,
    surface = DarkSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = BdGreenPrimary,
    onPrimary = Color.White,
    secondary = BdGoldAccent,
    onSecondary = Color.Black,
    tertiary = BdRedAccent,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = OnLightSurface,
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = OnLightSurface
)

@Composable
fun CholoBdTheme(
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
