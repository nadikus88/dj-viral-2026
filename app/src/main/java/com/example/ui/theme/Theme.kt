package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DjDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = ObsidianBackground,
    primaryContainer = CardElevatedSurface,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = TextPrimary,
    secondaryContainer = CardElevatedSurface,
    onSecondaryContainer = NeonPink,
    tertiary = NeonPurple,
    onTertiary = TextPrimary,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = CardDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardElevatedSurface,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = TextMuted,
    error = ErrorNeonRed,
    onError = TextPrimary
)

private val DjShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun DjViralTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DjDarkColorScheme,
        typography = AppTypography,
        shapes = DjShapes,
        content = content
    )
}
