package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Indigo500,
    onPrimary = Color.White,
    primaryContainer = Indigo950,
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = EmeraldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Indigo500,
    onTertiary = Color.White,
    background = BackgroundDark,          // bg-zinc-950
    onBackground = TextPrimaryDark,       // text-zinc-100
    surface = SurfaceDark,                // bg-zinc-900
    onSurface = TextPrimaryDark,          // text-zinc-100
    surfaceVariant = SurfaceMutedDark,    // bg-zinc-800
    onSurfaceVariant = TextSecondaryDark, // text-zinc-400
    outline = CardBorderDark,             // border-zinc-800
    outlineVariant = Color(0xFF3F3F46),
    error = CrimsonWrong
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = Color.White,
    primaryContainer = Indigo50,
    onPrimaryContainer = Indigo600,
    secondary = EmeraldSuccess,
    onSecondary = Color.White,
    secondaryContainer = EmeraldSoft,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Indigo600,
    onTertiary = Color.White,
    background = BackgroundLight,          // bg-slate-50
    onBackground = TextPrimaryLight,       // text-slate-900
    surface = SurfaceLight,                // bg-white
    onSurface = TextPrimaryLight,          // text-slate-900
    surfaceVariant = SurfaceMutedLight,    // bg-slate-100
    onSurfaceVariant = TextSecondaryLight, // text-slate-500
    outline = CardBorderLight,             // border-slate-200
    outlineVariant = Color(0xFFF1F5F9),
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
