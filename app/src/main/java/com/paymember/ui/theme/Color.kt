package com.paymember.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFF0A5C5A)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF2A6F97)
val LightBackground = Color(0xFFF4F7F8)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1B1F23)

val DarkPrimary = Color(0xFF57C7C2)
val DarkOnPrimary = Color(0xFF00201F)
val DarkSecondary = Color(0xFF89C5E8)
val DarkBackground = Color(0xFF101417)
val DarkSurface = Color(0xFF171D21)
val DarkOnSurface = Color(0xFFE2E8EE)

val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)
