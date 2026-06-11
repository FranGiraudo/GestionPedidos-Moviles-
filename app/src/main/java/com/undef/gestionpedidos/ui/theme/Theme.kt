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
    primary          = PurpleMain,
    onPrimary        = CardSurface,
    primaryContainer = Lavender,
    onPrimaryContainer = DarkBlue,

    secondary        = DarkBlue,
    onSecondary      = CardSurface,

    background       = BackgroundCream,
    onBackground     = TextPrimary,

    surface          = CardSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = AvatarBg,
    onSurfaceVariant = TextSecondary,
)

// No dark mode needed specifically for this mockup, fallback to light
private val DarkColorScheme = LightColorScheme

@Composable
fun GestionPedidosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Forzamos el esquema claro para mantener la estética del mockup siempre
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
