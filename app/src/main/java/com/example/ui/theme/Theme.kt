package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TestbookCyan,
    onPrimary = Color.White,
    primaryContainer = Indigo950,
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = TestbookBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = TestbookBlue,
    onTertiary = Color.White,
    background = BackgroundDark,          // bg-[#0F172A]
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,                // bg-[#1E293B]
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceMutedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark,             // border-[#334155]
    outlineVariant = Color(0xFF334155),
    error = CrimsonWrong
)

private val LightColorScheme = lightColorScheme(
    primary = TestbookBlue,               // #1A73E8
    onPrimary = Color.White,
    primaryContainer = Indigo50,
    onPrimaryContainer = TestbookBlue,
    secondary = TestbookCyan,             // #00A699
    onSecondary = Color.White,
    secondaryContainer = EmeraldSoft,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = TestbookCyan,
    onTertiary = Color.White,
    background = BackgroundLight,          // bg-[#F4F6F9]
    onBackground = TextPrimaryLight,       // #1E293B
    surface = SurfaceLight,                // bg-white (#FFFFFF)
    onSurface = TextPrimaryLight,          // #1E293B
    surfaceVariant = SurfaceMutedLight,
    onSurfaceVariant = TextSecondaryLight, // #64748B
    outline = CardBorderLight,             // border-[#E2E8F0]
    outlineVariant = Color(0xFFE2E8F0),
    error = CrimsonWrong
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
