package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = StadiumGreen,
    onPrimary = Color.Black,
    primaryContainer = StadiumGreenDark,
    onPrimaryContainer = Color.White,
    secondary = OddsGold,
    onSecondary = Color.Black,
    secondaryContainer = OddsGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = AccentBlue,
    background = PitchBlack,
    onBackground = TextPrimary,
    surface = PitchSurface,
    onSurface = TextPrimary,
    surfaceVariant = PitchSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = PitchBorder,
    error = CardRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
