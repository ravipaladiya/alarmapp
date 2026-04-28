package com.lumen.alarm.ui.theme

import androidx.compose.ui.graphics.Color

// ── Backgrounds ──────────────────────────────────────────────────────────────
val BgDeepest = Color(0xFF0A0E1F)   // Lockscreen, scrim
val BgApp     = Color(0xFF11162B)   // App background surface
val BgCard    = Color(0xFF181F3A)   // Elevated cards, sheets
val BgHover   = Color(0xFF232C4F)   // Hovered / pressed states

// ── Text / Ink ────────────────────────────────────────────────────────────────
val Ink0 = Color(0xFFF4F6FF)   // Primary headings, time displays
val Ink1 = Color(0xFFC9CEE8)   // Secondary body text
val Ink2 = Color(0xFF8189AD)   // Tertiary captions, meta
val Ink3 = Color(0xFF525B81)   // Quaternary — disabled, decorative only

// ── Accent palette ───────────────────────────────────────────────────────────
val IndigoAccent  = Color(0xFF7C8CFF)   // Primary actions, buttons, focus
val IndigoSoft    = Color(0xFFA5B1FF)   // Highlights, pill badges
val IndigoDeep    = Color(0xFF4D5ED4)   // Toggle active state
val AuroraAccent  = Color(0xFF6EE7D4)   // Success, streaks, sleep
val GoldAccent    = Color(0xFFF5CB6E)   // Coins, warnings, rewards
val RoseAccent    = Color(0xFFFF9BB3)   // Alarm, destructive, ringing
val LilacAccent   = Color(0xFFB598FF)   // Sleep, ambient, dream

// ── Borders ───────────────────────────────────────────────────────────────────
val LineSubtle  = Color(0x14B4C3FF)   // rgba(180,195,255,0.08)
val LineMedium  = Color(0x24B4C3FF)   // rgba(180,195,255,0.14)

// ── Glow / Overlay ────────────────────────────────────────────────────────────
val GlowIndigo = Color(0x337C8CFF)
val GlowAurora = Color(0x336EE7D4)
val GlowGold   = Color(0x33F5CB6E)
val GlowRose   = Color(0x33FF9BB3)

// ── Semantic ──────────────────────────────────────────────────────────────────
val Success  = AuroraAccent
val Warning  = GoldAccent
val Danger   = RoseAccent
val Premium  = GoldAccent
val LiveDot  = AuroraAccent

// ── Light theme overrides ──────────────────────────────────────────────────────
val LightBg0    = Color(0xFFF5F4FB)
val LightBg1    = Color(0xFFFFFFFF)
val LightBg2    = Color(0xFFF0EEF8)
val LightInk0   = Color(0xFF1A1D35)
val LightInk1   = Color(0xFF44497A)
val LightInk2   = Color(0xFF8189AD)

// ── Gradient pairs used throughout the app ───────────────────────────────────
val GradientIndigoStart  = Color(0xFF7C8CFF)
val GradientIndigoEnd    = Color(0xFF4D5ED4)
val GradientAuroraStart  = Color(0xFF6EE7D4)
val GradientAuroraEnd    = Color(0xFF4DBFAD)
val GradientGoldStart    = Color(0xFFF5CB6E)
val GradientGoldEnd      = Color(0xFFE8A83C)
val GradientRoseStart    = Color(0xFFFF9BB3)
val GradientRoseEnd      = Color(0xFFFF6B8A)
val GradientStreakStart  = Color(0xFFF5CB6E)
val GradientStreakEnd    = Color(0xFFFF9BB3)
