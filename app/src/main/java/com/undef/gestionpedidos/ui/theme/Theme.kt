package com.undef.gestionpedidos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primario: grafito oscuro
    primary             = Graphite900,
    onPrimary           = Color.White,
    primaryContainer    = Graphite100,
    onPrimaryContainer  = Graphite900,

    // Secundario: verde accent
    secondary           = Green600,
    onSecondary         = Color.White,
    secondaryContainer  = GreenSoft,
    onSecondaryContainer = Green700,

    // Terciario: terracota (warning / estados intermedios)
    tertiary            = Terra500,
    onTertiary          = Color.White,
    tertiaryContainer   = TerraSoft,
    onTertiaryContainer = Terra700,

    // Fondo y superficies
    background          = BackgroundLight,
    onBackground        = TextPrimaryLight,
    surface             = SurfaceLight,
    onSurface           = TextPrimaryLight,
    surfaceVariant      = Graphite100,
    onSurfaceVariant    = TextSecondaryLight,
    outline             = BorderLight,
    outlineVariant      = BorderInput,

    // Error (Material standard)
    error               = Color(0xFFB3261E),
    onError             = Color.White,
    errorContainer      = Color(0xFFF9DEDC),
    onErrorContainer    = Color(0xFF410E0B),
)

private val DarkColorScheme = darkColorScheme(
    // Primario: el grafito se invierte en dark — usamos texto claro sobre fondo oscuro
    primary             = TextPrimaryDark,
    onPrimary           = Graphite900,
    primaryContainer    = Graphite700,
    onPrimaryContainer  = TextPrimaryDark,

    // Secundario: verde más claro para contraste en dark
    secondary           = Green400,
    onSecondary         = Color(0xFF0E3D28),
    secondaryContainer  = Color(0xFF26282B),
    onSecondaryContainer = Green400,

    // Terciario
    tertiary            = Terra500,
    onTertiary          = Color.White,
    tertiaryContainer   = Color(0xFF3A2218),
    onTertiaryContainer = Terra500,

    // Fondo y superficies
    background          = BackgroundDark,
    onBackground        = TextPrimaryDark,
    surface             = SurfaceDark,
    onSurface           = TextPrimaryDark,
    surfaceVariant      = Color(0xFF26282B),
    onSurfaceVariant    = TextSecondaryDark,
    outline             = BorderDark,
    outlineVariant      = Color(0xFF3A3C40),

    // Error
    error               = Color(0xFFF2B8B5),
    onError             = Color(0xFF601410),
    errorContainer      = Color(0xFF8C1D18),
    onErrorContainer    = Color(0xFFF9DEDC),
)

@Composable
fun GestionPedidosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
