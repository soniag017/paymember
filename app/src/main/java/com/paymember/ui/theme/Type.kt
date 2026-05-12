package com.paymember.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DisplayFont = FontFamily.SansSerif
private val MonoFont = FontFamily.Monospace
private const val TabularNums = "tnum"

val PMTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 56.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = TabularNums
    ),
    displayMedium = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 40.sp,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = TabularNums
    ),
    headlineLarge = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleSmall = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    labelLarge = TextStyle(
        fontFamily = DisplayFont,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = TextStyle(
        fontFamily = MonoFont,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.9.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MonoFont,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.9.sp
    )
)
