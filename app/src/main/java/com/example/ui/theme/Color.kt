package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val ObsidianBackground = Color(0xFF0D0D12)
val CardDarkSurface = Color(0xFF181822)
val CardElevatedSurface = Color(0xFF222230)
val NeonCyan = Color(0xFF00F2FE)
val NeonPink = Color(0xFFFF007F)
val NeonPurple = Color(0xFF7B2FF7)

val TextPrimary = Color(0xFFF5F7FA)
val TextSecondary = Color(0xFFA0A3B5)
val TextMuted = Color(0xFF6B6F82)
val BorderSubtle = Color(0xFF2A2A3B)
val ErrorNeonRed = Color(0xFFFF4D6D)

val NeonTikTokGradient = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonPink)
)

val NeonTikTokDiagonalGradient = Brush.linearGradient(
    colors = listOf(NeonCyan, NeonPurple, NeonPink)
)
