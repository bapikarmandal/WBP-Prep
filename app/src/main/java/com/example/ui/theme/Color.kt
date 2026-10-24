package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Minimalist Slate, Zinc & Indigo Palette (Notion / Linear / Duolingo inspired)

// Primary Accents (Indigo)
val Indigo600 = Color(0xFF4F46E5)
val Indigo500 = Color(0xFF6366F1)
val Indigo50 = Color(0xFFEEF2FF)
val Indigo950 = Color(0xFF1E1B4B)

// Semantic Status Colors (Emerald for Success, Rose for Mistakes/Alerts, Amber for Pacer Warning)
val EmeraldSuccess = Color(0xFF059669) // emerald-600
val EmeraldDark = Color(0xFF10B981)    // emerald-500 for dark mode contrast
val EmeraldSoft = Color(0xFFD1FAE5)

val CrimsonWrong = Color(0xFFF43F5E)   // rose-500
val CrimsonSoft = Color(0xFFFFE4E6)

val AmberWarning = Color(0xFFF59E0B)   // amber-500

val PurpleReview = Color(0xFF8B5CF6)   // violet-500
val PurpleSoft = Color(0xFFEDE9FE)

// Light Mode (Slate)
val BackgroundLight = Color(0xFFF8FAFC)    // bg-slate-50
val SurfaceLight = Color(0xFFFFFFFF)       // bg-white
val SurfaceMutedLight = Color(0xFFF1F5F9)  // bg-slate-100 (Segmented pill background)
val CardBorderLight = Color(0xFFE2E8F0)    // border-slate-200
val TextPrimaryLight = Color(0xFF0F172A)   // text-slate-900
val TextSecondaryLight = Color(0xFF64748B) // text-slate-500
val TextMicroLight = Color(0xFF94A3B8)     // text-slate-400

// Dark Mode (True Minimal Zinc)
val BackgroundDark = Color(0xFF09090B)     // bg-zinc-950
val SurfaceDark = Color(0xFF18181B)        // bg-zinc-900
val SurfaceMutedDark = Color(0xFF27272A)   // bg-zinc-800 (Segmented pill background)
val CardBorderDark = Color(0xFF27272A)     // border-zinc-800
val TextPrimaryDark = Color(0xFFF4F4F5)    // text-zinc-100
val TextSecondaryDark = Color(0xFFA1A1AA)  // text-zinc-400
val TextMicroDark = Color(0xFF71717A)      // text-zinc-500

// Mapped Legacy Aliases (redirected to Clean Slate, Zinc & Indigo so no screen uses Khaki/Navy)
val NavyDeep = BackgroundDark
val NavyPrimary = Indigo600
val NavyLight = Indigo500
val NavySurfaceDark = SurfaceDark
val NavyCardDark = SurfaceMutedDark

val KhakiGold = Indigo600
val KhakiAmber = Indigo500
val KhakiLight = Indigo50
val KhakiAccentDark = Indigo500
