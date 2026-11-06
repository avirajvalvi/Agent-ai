package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = CharcoalElevated,
    onPrimaryContainer = TerracottaLight,
    secondary = TaupeTextSecondary,
    onSecondary = CharcoalDark,
    secondaryContainer = CharcoalElevated,
    onSecondaryContainer = CreamTextPrimary,
    background = CharcoalDark,
    onBackground = CreamTextPrimary,
    surface = CharcoalSurface,
    onSurface = CreamTextPrimary,
    surfaceVariant = CharcoalElevated,
    onSurfaceVariant = TaupeTextSecondary,
    outline = CharcoalBorder,
    outlineVariant = CharcoalElevated,
    tertiary = CyberEmerald,
    onTertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaDark,
    onPrimary = Color.White,
    primaryContainer = IvorySurfaceVariant,
    onPrimaryContainer = TerracottaDark,
    secondary = TaupeMuted,
    onSecondary = Color.White,
    secondaryContainer = IvorySurfaceVariant,
    onSecondaryContainer = DeepCharcoalText,
    background = IvoryBackground,
    onBackground = DeepCharcoalText,
    surface = IvorySurface,
    onSurface = DeepCharcoalText,
    surfaceVariant = IvorySurfaceVariant,
    onSurfaceVariant = TaupeMuted,
    outline = IvoryBorder,
    outlineVariant = IvorySurfaceVariant,
    tertiary = TerracottaPrimary,
    onTertiary = Color.White
)

@Composable
fun SecMentorTheme(
    darkTheme: Boolean = true, // Warm dark editorial default inspired by Claude screenshots
    dynamicColor: Boolean = false, // Keep signature warm styling consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
