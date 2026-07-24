package com.undef.gestionpedidos.ui.feature.statistics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.ui.components.MoneyText
import com.undef.gestionpedidos.ui.components.SkeletonCard
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.Green600
import com.undef.gestionpedidos.ui.theme.Green700
import com.undef.gestionpedidos.ui.theme.GreenSoft
import com.undef.gestionpedidos.ui.theme.MonoFamily
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_estadisticas_de_vent)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite900,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) { SkeletonCard() }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── KPI Cards ───────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Graphite900),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(com.undef.gestionpedidos.R.string.txt_ventas_totales),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            MoneyText(
                                text = uiState.ventasTotalesMes,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(com.undef.gestionpedidos.R.string.txt_total_clientes),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            MoneyText(
                                text = uiState.nuevosClientesMes,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W700
                            )
                        }
                    }
                }

                // ── Gráfico de barras Ventas 7 días ─────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(com.undef.gestionpedidos.R.string.txt_ventas_ltimos_7_d_as),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.W600
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val allZero = uiState.dailySales.all { it.amount == 0.0 }
                        if (uiState.dailySales.isEmpty() || allZero) {
                            ChartEmptyState()
                        } else {
                            AnimatedBarChart(dailySales = uiState.dailySales)
                        }
                    }
                }

                // ── Productos más vendidos ───────────────────────────────────
                if (uiState.topProducts.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(com.undef.gestionpedidos.R.string.txt_productos_mas_vendid),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W600
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            uiState.topProducts.forEach { prod ->
                                TopItem(
                                    name = prod.name,
                                    progress = prod.fraction,
                                    value = "${prod.quantity} unid.",
                                    isMoney = false
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // ── Mejores clientes ─────────────────────────────────────────
                if (uiState.topClients.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(com.undef.gestionpedidos.R.string.txt_mejores_clientes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W600
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            uiState.topClients.forEach { client ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Graphite900),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = client.name.firstOrNull()?.uppercase() ?: "?",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.W600
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = client.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MoneyText(
                                        text = "$ ${String.format("%.0f", client.spent)}",
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.W600
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Empty state ──────────────────────────────────────────────────────────────

@Composable
private fun ChartEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.BarChart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Todavía no hay ventas en este período",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

// ── Animated bar chart ───────────────────────────────────────────────────────

@Composable
fun AnimatedBarChart(dailySales: List<DailySales>) {
    // Animación de entrada: 0f → 1f en 400ms
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(dailySales) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(durationMillis = 450))
    }

    val anim = animProgress.value
    val maxAmount = dailySales.maxOfOrNull { it.amount } ?: 1.0

    // Índice de la barra seleccionada por touch (-1 = ninguna)
    var selectedIndex by remember { mutableIntStateOf(-1) }

    // Posición del tooltip (px desde arriba izquierda del Canvas)
    var tooltipOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current

    // Colores
    val barColor = Green600
    val barMaxColor = Green700
    val barDimColor = GreenSoft
    val guideDash = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val tooltipBg = Graphite900
    val tooltipText = Color.White

    // Formato de moneda para el tooltip
    val nf = NumberFormat.getNumberInstance(Locale("es", "AR"))

    val chartHeight = 160.dp
    val chartHeightPx = with(density) { chartHeight.toPx() }

    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .pointerInput(dailySales) {
                    detectTapGestures { tapOffset ->
                        val barSlot = size.width / dailySales.size
                        val tapped = (tapOffset.x / barSlot).toInt().coerceIn(0, dailySales.size - 1)
                        selectedIndex = if (selectedIndex == tapped) -1 else tapped
                        tooltipOffset = tapOffset
                    }
                }
        ) {
            val barSlotWidth = size.width / dailySales.size
            val cornerRadius = 6.dp.toPx()
            val guideY = size.height * 0.05f  // línea guía al nivel del pico

            // Línea guía punteada en el valor máximo
            drawLine(
                color = guideDash,
                start = Offset(0f, guideY),
                end = Offset(size.width, guideY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )

            dailySales.forEachIndexed { index, sale ->
                val isMax = sale.amount == maxAmount
                val isSelected = index == selectedIndex
                val hasSelection = selectedIndex != -1

                val fraction = if (maxAmount > 0) (sale.amount / maxAmount).toFloat() else 0f
                val barHeightTarget = (size.height - guideY) * fraction.coerceAtLeast(0.02f)
                val barHeightAnimated = barHeightTarget * anim

                val barWidth = barSlotWidth * 0.55f
                val barLeft = barSlotWidth * index + (barSlotWidth - barWidth) / 2f
                val barTop = size.height - barHeightAnimated

                val color = when {
                    hasSelection && !isSelected -> barDimColor
                    isMax -> barMaxColor
                    else -> barColor
                }

                // Barra con esquinas superiores redondeadas
                val barPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(
                                Offset(barLeft, barTop),
                                Size(barWidth, barHeightAnimated)
                            ),
                            topLeft = CornerRadius(cornerRadius),
                            topRight = CornerRadius(cornerRadius),
                            bottomLeft = CornerRadius(0f),
                            bottomRight = CornerRadius(0f)
                        )
                    )
                }
                drawPath(barPath, color = color)

                // Indicador del día máximo (pequeño punto arriba de la barra)
                if (isMax && anim > 0.9f) {
                    drawCircle(
                        color = barMaxColor,
                        radius = 4.dp.toPx(),
                        center = Offset(barLeft + barWidth / 2f, barTop - 8.dp.toPx())
                    )
                }
            }
        }

        // Tooltip flotante cuando hay una selección
        if (selectedIndex in dailySales.indices) {
            val sale = dailySales[selectedIndex]
            val formatted = nf.format(sale.amount.toLong())
            val barSlotWidth = with(density) { 1.dp.toPx() } // se recalcula abajo con BoxWithConstraints

            Box(
                modifier = Modifier
                    .offset {
                        val slotPx = (tooltipOffset.x).toInt()
                        val tooltipWidthPx = 120.dp.toPx().toInt()
                        val x = (slotPx - tooltipWidthPx / 2).coerceAtLeast(0)
                        val y = (tooltipOffset.y - 64.dp.toPx()).toInt().coerceAtLeast(0)
                        IntOffset(x, y)
                    }
                    .background(tooltipBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = sale.day,
                        style = MaterialTheme.typography.labelSmall,
                        color = tooltipText.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$ $formatted",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFamily),
                        color = tooltipText,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Eje X
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dailySales.forEach { sale ->
            Text(
                text = sale.day,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Top item (productos / clientes) ──────────────────────────────────────────

@Composable
fun TopItem(name: String, progress: Float, value: String, isMoney: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            MoneyText(
                text = value,
                color = Green600,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(2.dp)),
            color = Green600,
            trackColor = GreenSoft
        )
    }
}
