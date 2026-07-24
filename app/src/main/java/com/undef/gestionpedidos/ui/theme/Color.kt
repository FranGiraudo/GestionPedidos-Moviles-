package com.undef.gestionpedidos.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Paleta Base Grafito ────────────────────────────────────────────────────
val Graphite900 = Color(0xFF2B2E33)   // Primary header, TopAppBar, FAB
val Graphite700 = Color(0xFF3D4148)   // Avatar background oscuro sobre header
val Graphite500 = Color(0xFF6B6A64)   // Texto neutro / inactivo
val Graphite100 = Color(0xFFF0EFEB)   // Fondo neutro suave

// ─── Paleta Verde Accent ─────────────────────────────────────────────────────
val Green700     = Color(0xFF1F6A48)   // Texto verde oscuro (AA sobre fondo claro)
val Green600     = Color(0xFF2E7D5B)   // Acento principal (botón CTA, ítems activos)
val Green400     = Color(0xFF4ABE87)   // Acento dark mode
val GreenSoft    = Color(0xFFE4F1EA)   // Fondo suave verde (sección agregar producto)
val GreenBorder  = Color(0xFFD8E5DD)   // Borde sección verde

// ─── Paleta Terracota (warning / intermedio) ─────────────────────────────────
val Terra700     = Color(0xFF9A4F26)   // Texto terracota oscuro
val Terra500     = Color(0xFFC77B4A)   // Color terracota medio
val TerraSoft    = Color(0xFFFBEDE3)   // Fondo suave terracota

// ─── Superficies y Fondos (Light) ────────────────────────────────────────────
val BackgroundLight  = Color(0xFFFBFAF8)
val SurfaceLight     = Color(0xFFFFFFFF)
val BorderLight      = Color(0xFFE7E5DF)
val BorderInput      = Color(0xFFDAD8D1)

// ─── Superficies y Fondos (Dark) ─────────────────────────────────────────────
val BackgroundDark   = Color(0xFF17181A)
val SurfaceDark      = Color(0xFF1E2023)
val BorderDark       = Color(0xFF2C2E31)

// ─── Texto ───────────────────────────────────────────────────────────────────
val TextPrimaryLight    = Color(0xFF1A1C1F)
val TextSecondaryLight  = Color(0xFF8B8A85)
val TextPrimaryDark     = Color(0xFFF2F1EE)
val TextSecondaryDark   = Color(0xFF8E9095)

// ─── Status Pills ────────────────────────────────────────────────────────────
// Entregado → verde
val StatusGreenBg   = GreenSoft
val StatusGreenText = Green700

// Despachado / En preparación → terracota
val StatusOrangeBg   = TerraSoft
val StatusOrangeText = Terra700

// Borrador / Inactivo → neutro
val StatusNeutralBg   = Graphite100
val StatusNeutralText = Graphite500

// Cancelado → error Material (se toma del colorScheme.error)

// ─── Legados re-exportados para compatibilidad con components existentes ──────
/** @deprecated Usar tokens semánticos del theme */
val AvatarBg    = Graphite700
val AvatarText  = Color(0xFFD0D3D8)
val CardSurface = SurfaceLight
val TextPrimary = TextPrimaryLight
val TextSecondary = TextSecondaryLight
