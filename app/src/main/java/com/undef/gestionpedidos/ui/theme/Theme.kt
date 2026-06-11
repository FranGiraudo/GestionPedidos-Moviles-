package com.undef.gestionpedidos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Blue700,
    onPrimary        = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,

    secondary        = Orange600,
    onSecondary      = White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Blue900,

    tertiary         = Green700,
    onTertiary       = White,
    tertiaryContainer = Green100,
    onTertiaryContainer = Neutral900,

    background       = Neutral50,
    onBackground     = Neutral900,

    surface          = White,
    onSurface        = Neutral900,
    surfaceVariant   = Neutral100,
    onSurfaceVariant = Neutral700,

    outline          = Neutral300,
    outlineVariant   = Neutral100,

    error            = Red700,
    onError          = White,
    errorContainer   = Red100,
    onErrorContainer = Neutral900
)

private val DarkColorScheme = darkColorScheme(
    primary          = Blue400,
    onPrimary        = Blue900,
    primaryContainer = Blue800,
    onPrimaryContainer = Blue50,

    secondary        = Orange400,
    onSecondary      = Blue900,
    secondaryContainer = Orange600,
    onSecondaryContainer = White,

    tertiary         = Green100,
    onTertiary       = Green700,
    tertiaryContainer = Green700,
    onTertiaryContainer = Green100,

    background       = Neutral900,
    onBackground     = Neutral100,

    surface          = Color(0xFF1C2431),
    onSurface        = Neutral100,
    surfaceVariant   = Color(0xFF253144),
    onSurfaceVariant = Neutral300,

    outline          = Neutral500,
    outlineVariant   = Neutral700,

    error            = Red100,
    onError          = Red700,
    errorContainer   = Red700,
    onErrorContainer = Red100
)

@Composable
fun GestionPedidosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
