package com.lumen.alarm.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LumenDarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = BgDeepest,
    primaryContainer = IndigoDeep,
    onPrimaryContainer = IndigoSoft,
    secondary = AuroraAccent,
    onSecondary = BgDeepest,
    secondaryContainer = Color(0xFF1A3D38),
    onSecondaryContainer = AuroraAccent,
    tertiary = LilacAccent,
    onTertiary = BgDeepest,
    background = BgApp,
    onBackground = Ink0,
    surface = BgCard,
    onSurface = Ink0,
    surfaceVariant = BgHover,
    onSurfaceVariant = Ink1,
    outline = LineSubtle,
    outlineVariant = LineMedium,
    error = RoseAccent,
    onError = BgDeepest,
    errorContainer = Color(0xFF3D1A24),
    onErrorContainer = RoseAccent,
    scrim = BgDeepest,
    inverseSurface = Ink0,
    inverseOnSurface = BgDeepest,
    inversePrimary = IndigoDeep,
    surfaceTint = IndigoAccent,
)

private val LumenLightColorScheme = lightColorScheme(
    primary = IndigoDeep,
    onPrimary = Color.White,
    primaryContainer = IndigoSoft,
    onPrimaryContainer = IndigoDeep,
    secondary = Color(0xFF2A9D8F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F5F2),
    onSecondaryContainer = Color(0xFF1A5C55),
    tertiary = Color(0xFF7C5CBF),
    onTertiary = Color.White,
    background = LightBg0,
    onBackground = LightInk0,
    surface = LightBg1,
    onSurface = LightInk0,
    surfaceVariant = LightBg2,
    onSurfaceVariant = LightInk1,
    outline = Color(0xFFD0CCEE),
    error = Color(0xFFD32F55),
    onError = Color.White,
    scrim = Color(0x99000000),
)

// Custom extended colors accessible anywhere via LocalLumenColors
data class LumenExtendedColors(
    val aurora: Color,
    val gold: Color,
    val rose: Color,
    val lilac: Color,
    val indigoSoft: Color,
    val indigoDeep: Color,
    val bgDeepest: Color,
    val bgApp: Color,
    val bgCard: Color,
    val bgHover: Color,
    val ink0: Color,
    val ink1: Color,
    val ink2: Color,
    val ink3: Color,
    val lineSubtle: Color,
    val lineMedium: Color,
    val glowIndigo: Color,
    val glowAurora: Color,
    val glowGold: Color,
    val glowRose: Color,
    val isDark: Boolean,
)

val LumenDarkExtended = LumenExtendedColors(
    aurora = AuroraAccent,
    gold = GoldAccent,
    rose = RoseAccent,
    lilac = LilacAccent,
    indigoSoft = IndigoSoft,
    indigoDeep = IndigoDeep,
    bgDeepest = BgDeepest,
    bgApp = BgApp,
    bgCard = BgCard,
    bgHover = BgHover,
    ink0 = Ink0,
    ink1 = Ink1,
    ink2 = Ink2,
    ink3 = Ink3,
    lineSubtle = LineSubtle,
    lineMedium = LineMedium,
    glowIndigo = GlowIndigo,
    glowAurora = GlowAurora,
    glowGold = GlowGold,
    glowRose = GlowRose,
    isDark = true,
)

val LumenLightExtended = LumenExtendedColors(
    aurora = Color(0xFF2A9D8F),
    gold = Color(0xFFD4870E),
    rose = Color(0xFFD32F55),
    lilac = Color(0xFF7C5CBF),
    indigoSoft = Color(0xFF6672E8),
    indigoDeep = IndigoDeep,
    bgDeepest = LightBg2,
    bgApp = LightBg0,
    bgCard = LightBg1,
    bgHover = LightBg2,
    ink0 = LightInk0,
    ink1 = LightInk1,
    ink2 = LightInk2,
    ink3 = Color(0xFFB0AECC),
    lineSubtle = Color(0x14000000),
    lineMedium = Color(0x20000000),
    glowIndigo = Color(0x224D5ED4),
    glowAurora = Color(0x222A9D8F),
    glowGold = Color(0x22D4870E),
    glowRose = Color(0x22D32F55),
    isDark = false,
)

val LocalLumenColors = staticCompositionLocalOf { LumenDarkExtended }

@Composable
fun LumenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LumenDarkColorScheme
        else -> LumenLightColorScheme
    }
    val extendedColors = if (darkTheme) LumenDarkExtended else LumenLightExtended

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalLumenColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LumenTypography,
            shapes = LumenShapes,
            content = content,
        )
    }
}

// Convenience accessor
val MaterialTheme.lumen: LumenExtendedColors
    @Composable get() = LocalLumenColors.current
