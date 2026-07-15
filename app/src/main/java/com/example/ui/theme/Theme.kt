package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoyalTealDark,
    secondary = SlateBlueDark,
    tertiary = AmberAccentDark,
    background = DeepSpaceDark,
    surface = CardBgDark,
    onPrimary = Color(0xFF071B24),
    onSecondary = Color(0xFF132A38),
    onTertiary = Color(0xFF331400),
    onBackground = Color(0xFFE1E5E8),
    onSurface = Color(0xFFE1E5E8),
    surfaceContainer = Color(0xFF1C2738)
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalTealLight,
    secondary = SlateBlueLight,
    tertiary = AmberAccentLight,
    background = IceBgLight,
    surface = CardBgLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1E262A),
    onSurface = Color(0xFF1E262A),
    surfaceContainer = Color(0xFFEAF0F4)
)

@Composable
fun MyApplicationTheme(
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
