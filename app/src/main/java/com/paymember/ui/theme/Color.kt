package com.paymember.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val ForestGreen = Color(0xFF1F4634)
val ForestSoft = Color(0xFFE6EDDD)
val ForestInk = Color(0xFF0E2A1F)
val OnBrand = Color.White

val ForestGreenDark = Color(0xFF9CD8B5)
val ForestSoftDark = Color(0xFF22332A)
val ForestInkDark = Color(0xFFDEF1E4)
val OnBrandDark = Color(0xFF0B1A12)

val Bone = Color(0xFFEFEDE6)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceTint1 = Color(0xFFFAF7EE)
val SurfaceTint2 = Color(0xFFF5F1E4)
val Ink = Color(0xFF0F1010)
val Ink2 = Color(0xFF3A3B3D)
val InkMuted = Color(0xFF8A8A85)
val Hairline = Color(0xFFE5E1D2)
val Hairline2 = Color(0xFFEDEAE0)

val BoneDark = Color(0xFF14130F)
val SurfaceDark = Color(0xFF1E1C17)
val SurfaceTint1Dk = Color(0xFF25221C)
val SurfaceTint2Dk = Color(0xFF2C281F)
val InkDark = Color(0xFFF1EDDE)
val Ink2Dark = Color(0xFFBFB9A8)
val InkMutedDark = Color(0xFF7E796C)
val HairlineDark = Color(0xFF2F2B22)
val Hairline2Dark = Color(0xFF3A3527)

val Coral = Color(0xFFE8916B)
val Lavender = Color(0xFFB5AEE6)
val Sky = Color(0xFFA6C6E8)
val Butter = Color(0xFFE8D08C)
val Moss = Color(0xFF9CB87A)
val Rose = Color(0xFFE4B5C5)

val Success = Color(0xFF2F7E5C)
val Danger = Color(0xFFC24B4B)
val SuccessDark = Color(0xFF4FB089)
val DangerDark = Color(0xFFE27A7A)

val PayMemberLightScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = OnBrand,
    primaryContainer = ForestSoft,
    onPrimaryContainer = ForestInk,
    secondary = Ink,
    onSecondary = SurfaceWhite,
    secondaryContainer = SurfaceTint2,
    onSecondaryContainer = Ink,
    background = Bone,
    onBackground = Ink,
    surface = SurfaceWhite,
    onSurface = Ink,
    surfaceVariant = SurfaceTint2,
    onSurfaceVariant = InkMuted,
    outline = Hairline,
    outlineVariant = Hairline2,
    error = Danger
)

val PayMemberDarkScheme = darkColorScheme(
    primary = ForestGreenDark,
    onPrimary = OnBrandDark,
    primaryContainer = ForestSoftDark,
    onPrimaryContainer = ForestInkDark,
    secondary = InkDark,
    onSecondary = BoneDark,
    secondaryContainer = SurfaceTint2Dk,
    onSecondaryContainer = InkDark,
    background = BoneDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceTint2Dk,
    onSurfaceVariant = InkMutedDark,
    outline = HairlineDark,
    outlineVariant = Hairline2Dark,
    error = DangerDark
)
