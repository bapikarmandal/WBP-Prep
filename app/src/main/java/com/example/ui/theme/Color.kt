package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Official Testbook App Palette for West Bengal Police (WBP) & Kolkata Police (KP)

// Primary Accents (Testbook Cyan-Blue & Royal Blue)
val TestbookCyan = Color(0xFF00A699)
val TestbookBlue = Color(0xFF1A73E8)
val Indigo600 = Color(0xFF1A73E8)
val Indigo500 = Color(0xFF00A699)
val Indigo50 = Color(0xFFE6F6F5)
val Indigo950 = Color(0xFF0D2B45)

// Semantic Status Colors (Emerald for Success, Rose for Mistakes/Alerts, Amber for Pacer Warning)
val EmeraldSuccess = Color(0xFF00A699) // Testbook Cyan-Emerald
val EmeraldDark = Color(0xFF10B981)    // emerald-500 for dark mode contrast
val EmeraldSoft = Color(0xFFD1FAE5)

val CrimsonWrong = Color(0xFFF43F5E)   // rose-500
val CrimsonSoft = Color(0xFFFFE4E6)

val AmberWarning = Color(0xFFF59E0B)   // amber-500

val PurpleReview = Color(0xFF8B5CF6)   // violet-500
val PurpleSoft = Color(0xFFEDE9FE)

// Light Mode (Default Official Testbook Theme)
val BackgroundLight = Color(0xFFF4F6F9)    // bg-[#F4F6F9] crisp slate background
val SurfaceLight = Color(0xFFFFFFFF)       // bg-white pure white cards
val SurfaceMutedLight = Color(0xFFEEF2F6)  // subtle pill background
val CardBorderLight = Color(0xFFE2E8F0)    // border-[#E2E8F0] subtle borders
val TextPrimaryLight = Color(0xFF1E293B)   // text-[#1E293B] dark slate text
val TextSecondaryLight = Color(0xFF64748B) // text-[#64748B] muted labels
val TextMicroLight = Color(0xFF94A3B8)     // text-slate-400

// Dark Mode (Deep Clean Slate)
val BackgroundDark = Color(0xFF0F172A)     // bg-[#0F172A] deep clean slate
val SurfaceDark = Color(0xFF1E293B)        // bg-[#1E293B] elevated cards
val SurfaceMutedDark = Color(0xFF26334D)   // elevated pill surface
val CardBorderDark = Color(0xFF334155)     // border-[#334155] subtle border
val TextPrimaryDark = Color(0xFFF8FAFC)    // text-slate-50
val TextSecondaryDark = Color(0xFF94A3B8)  // text-slate-400
val TextMicroDark = Color(0xFF64748B)      // text-slate-500

// Mapped Legacy Aliases
val NavyDeep = BackgroundDark
val NavyPrimary = TestbookBlue
val NavyLight = TestbookCyan
val NavySurfaceDark = SurfaceDark
val NavyCardDark = SurfaceMutedDark

val KhakiGold = TestbookBlue
val KhakiAmber = TestbookCyan
val KhakiLight = Indigo50
val KhakiAccentDark = TestbookCyan
