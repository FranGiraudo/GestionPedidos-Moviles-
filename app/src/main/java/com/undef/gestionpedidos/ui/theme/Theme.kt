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

private val LightColorScheme = lightColorScheme(
    primary          = Primary500,
    onPrimary        = White,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,

    secondary        = Accent500,
    onSecondary      = White,
    secondaryContainer = Accent100,
    onSecondaryContainer = Primary900,

    tertiary         = Success500,
    onTertiary       = White,
    tertiaryContainer = Success100,
    onTertiaryContainer = Slate900,

    background       = Slate50,
    onBackground     = Slate900,

    surface          = White,
    onSurface        = Slate900,
    surfaceVariant   = Slate100,
    onSurfaceVariant = Slate700,

    outline          = Slate300,
    outlineVariant   = Slate100,

    error            = Error500,
    onError          = White,
    errorContainer   = Error100,
    onErrorContainer = Slate900
)

private val DarkColorScheme = darkColorScheme(
    primary          = Primary400,
    onPrimary        = Slate900,
    primaryContainer = Primary800,
    onPrimaryContainer = Primary100,

    secondary        = Accent400,
    onSecondary      = Slate900,
    secondaryContainer = Accent600,
    onSecondaryContainer = White,

    tertiary         = Success100,
    onTertiary       = Success700,
    tertiaryContainer = Success700,
    onTertiaryContainer = Success100,

    background       = Slate900,
    onBackground     = Slate100,

    surface          = Slate800,
    onSurface        = Slate100,
    surfaceVariant   = Primary700,
    onSurfaceVariant = Slate300,

    outline          = Slate500,
    outlineVariant   = Slate700,

    error            = Error100,
    onError          = Error700,
    errorContainer   = Error700,
    onErrorContainer = Error100
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
