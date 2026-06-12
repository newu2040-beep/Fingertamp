package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object FingerThemeColors {
    // 1. Cyber Blue
    val CyberBluePrimary = Color(0xFF00F0FF) // neon high-tech cyan
    val CyberBlueSecondary = Color(0xFF007A99)
    val CyberBlueBackground = Color(0xFF050608) // deep space black
    val CyberBlueSurface = Color(0xFF0D0F14) // slate translucent overlay
    
    // 2. Aurora Purple
    val AuroraPurplePrimary = Color(0xFFBF5AF2) // aurora purple-pink
    val AuroraPurpleSecondary = Color(0xFF6E0DFF)
    val AuroraPurpleBackground = Color(0xFF050608)
    val AuroraPurpleSurface = Color(0xFF0E0E15)

    // 3. Graphite Black
    val GraphiteBlackPrimary = Color(0xFFE5E5EA) // pure aluminum/titanium
    val GraphiteBlackSecondary = Color(0xFF3A3A3C)
    val GraphiteBlackBackground = Color(0xFF050608)
    val GraphiteBlackSurface = Color(0xFF0E0E10)

    // 4. Emerald Glass
    val EmeraldGlassPrimary = Color(0xFF34C759) // vibrant emerald green
    val EmeraldGlassSecondary = Color(0xFF248A3D)
    val EmeraldGlassBackground = Color(0xFF050608)
    val EmeraldGlassSurface = Color(0xFF0D120E)

    // 5. Titanium Silver
    val TitaniumSilverPrimary = Color(0xFF8E8E93) // cool metallic
    val TitaniumSilverSecondary = Color(0xFFAEAEB2)
    val TitaniumSilverBackground = Color(0xFF050608)
    val TitaniumSilverSurface = Color(0xFF111316)

    // 6. Sunset Gold
    val SunsetGoldPrimary = Color(0xFFFFCC00) // royal warm gold
    val SunsetGoldSecondary = Color(0xFFC79A00)
    val SunsetGoldBackground = Color(0xFF050608)
    val SunsetGoldSurface = Color(0xFF13110E)

    // Standalone Fingerprint Animation Themes
    val ThemeColorsMap = mapOf(
        "Neon Blue" to listOf(Color(0xFF007AFF), Color(0xFF00F0FF)),
        "Emerald Green" to listOf(Color(0xFF34C759), Color(0xFF00E676)),
        "Cyber Purple" to listOf(Color(0xFFBF5AF2), Color(0xFFE040FB)),
        "Gold Glow" to listOf(Color(0xFFFFCC00), Color(0xFFFFD700)),
        "Crimson Red" to listOf(Color(0xFFFF3B30), Color(0xFFFF5252)),
        "Arctic White" to listOf(Color(0xFFE5E5EA), Color(0xFFFFFFFF)),
        "Ocean Cyan" to listOf(Color(0xFF30D158), Color(0xFF00E5FF)),
        "Sunset Orange" to listOf(Color(0xFFFF9500), Color(0xFFFF5722)),
        "Graphite Black" to listOf(Color(0xFF3A3A3C), Color(0xFF8E8E93)),
        "Aurora Gradient" to listOf(Color(0xFFBF5AF2), Color(0xFF00F0FF), Color(0xFF34C759))
    )

    fun getColorSchemeForPremium(themeName: String, isDark: Boolean): ColorScheme {
        return when (themeName) {
            "Cyber Blue" -> darkColorScheme(
                primary = CyberBluePrimary,
                secondary = CyberBlueSecondary,
                background = CyberBlueBackground,
                surface = CyberBlueSurface,
                surfaceVariant = Color(0xFF18223C),
                outline = CyberBluePrimary.copy(alpha = 0.3f),
                onPrimary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
            "Aurora Purple" -> darkColorScheme(
                primary = AuroraPurplePrimary,
                secondary = AuroraPurpleSecondary,
                background = AuroraPurpleBackground,
                surface = AuroraPurpleSurface,
                surfaceVariant = Color(0xFF211640),
                outline = AuroraPurplePrimary.copy(alpha = 0.3f),
                onPrimary = Color.White,
                onBackground = Color.White,
                onSurface = Color.White
            )
            "Graphite Black" -> darkColorScheme(
                primary = GraphiteBlackPrimary,
                secondary = GraphiteBlackSecondary,
                background = GraphiteBlackBackground,
                surface = GraphiteBlackSurface,
                surfaceVariant = Color(0xFF2C2C2E),
                outline = Color.DarkGray,
                onPrimary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
            "Emerald Glass" -> darkColorScheme(
                primary = EmeraldGlassPrimary,
                secondary = EmeraldGlassSecondary,
                background = EmeraldGlassBackground,
                surface = EmeraldGlassSurface,
                surfaceVariant = Color(0xFF182D20),
                outline = EmeraldGlassPrimary.copy(alpha = 0.3f),
                onPrimary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
            "Titanium Silver" -> darkColorScheme(
                primary = TitaniumSilverPrimary,
                secondary = TitaniumSilverSecondary,
                background = TitaniumSilverBackground,
                surface = TitaniumSilverSurface,
                surfaceVariant = Color(0xFF323B41),
                outline = TitaniumSilverPrimary.copy(alpha = 0.3f),
                onPrimary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
            "Sunset Gold" -> darkColorScheme(
                primary = SunsetGoldPrimary,
                secondary = SunsetGoldSecondary,
                background = SunsetGoldBackground,
                surface = SunsetGoldSurface,
                surfaceVariant = Color(0xFF302617),
                outline = SunsetGoldPrimary.copy(alpha = 0.3f),
                onPrimary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.White
            )
            else -> {
                // AMOLED or Fallback Standard dark/light
                if (themeName == "AMOLED" || !isDark) {
                    if (themeName == "AMOLED") {
                        darkColorScheme(
                            primary = Color(0xFF00FF90),
                            secondary = Color(0xFF009955),
                            background = Color.Black,
                            surface = Color(0xFF0E0E0E),
                            surfaceVariant = Color(0xFF161616),
                            outline = Color(0xFF333333),
                            onPrimary = Color.Black,
                            onBackground = Color.White,
                            onSurface = Color.White
                        )
                    } else {
                        // Light Mode
                        lightColorScheme(
                            primary = Color(0xFF0066EE),
                            secondary = Color(0xFF5599FF),
                            background = Color(0xFFF2F5FA),
                            surface = Color.White,
                            surfaceVariant = Color(0xFFE1E8F5),
                            outline = Color.LightGray,
                            onPrimary = Color.White,
                            onBackground = Color.Black,
                            onSurface = Color.Black
                        )
                    }
                } else {
                    // Default Dark Mode
                    darkColorScheme(
                        primary = Color(0xFF00E5FF),
                        secondary = Color(0xFF00838F),
                        background = Color(0xFF121212),
                        surface = Color(0xFF1E1E1E),
                        surfaceVariant = Color(0xFF2C2C2C),
                        outline = Color(0xFF3D3D3D),
                        onPrimary = Color.Black,
                        onBackground = Color.White,
                        onSurface = Color.White
                    )
                }
            }
        }
    }

    // Returns a nice premium brush background matching the theme
    fun getAmbientBackgroundBrush(themeName: String, isDark: Boolean): Brush {
        if (!isDark) {
            return Brush.verticalGradient(
                colors = listOf(Color(0xFFE6EDFA), Color(0xFFFAFBFE))
            )
        }
        val baseBg = Color(0xFF050608)
        return when (themeName) {
            "Cyber Blue" -> Brush.radialGradient(
                colors = listOf(Color(0xFF0A192F), baseBg),
                radius = 1200f
            )
            "Aurora Purple" -> Brush.radialGradient(
                colors = listOf(Color(0xFF160E30), baseBg),
                radius = 1200f
            )
            "Graphite Black" -> Brush.radialGradient(
                colors = listOf(Color(0xFF18181B), baseBg),
                radius = 1200f
            )
            "Emerald Glass" -> Brush.radialGradient(
                colors = listOf(Color(0xFF0C1D13), baseBg),
                radius = 1200f
            )
            "Titanium Silver" -> Brush.radialGradient(
                colors = listOf(Color(0xFF1E2124), baseBg),
                radius = 1200f
            )
            "Sunset Gold" -> Brush.radialGradient(
                colors = listOf(Color(0xFF241C10), baseBg),
                radius = 1200f
            )
            "AMOLED" -> Brush.verticalGradient(
                colors = listOf(baseBg, baseBg)
            )
            else -> Brush.radialGradient(
                colors = listOf(Color(0xFF12141C), baseBg),
                radius = 1200f
            )
        }
    }
}
