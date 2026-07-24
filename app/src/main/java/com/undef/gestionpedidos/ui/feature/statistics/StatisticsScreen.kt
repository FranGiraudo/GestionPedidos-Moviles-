package com.undef.gestionpedidos.ui.feature.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.ui.components.MoneyText
import com.undef.gestionpedidos.ui.components.SkeletonCard
import com.undef.gestionpedidos.ui.theme.GreenSoft
import com.undef.gestionpedidos.ui.theme.Green400
import com.undef.gestionpedidos.ui.theme.Green600
import com.undef.gestionpedidos.ui.theme.Green700
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily

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
                    // Ventas totales: card grafito destacada
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
                    // Total clientes: card surface
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
                if (uiState.dailySales.isNotEmpty()) {
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
                            val maxSale = uiState.dailySales.maxOfOrNull { it.amount } ?: 1.0
                            Row(
                                modifier = Modifier.fillMaxWidth().height(160.dp).padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                uiState.dailySales.forEach { dailySale ->
                                    val isMax = dailySale.amount == maxSale
                                    val heightFraction = if (maxSale > 0) (dailySale.amount / maxSale).toFloat() else 0f
                                    val barHeight = (110 * heightFraction.coerceAtLeast(0.05f)).dp
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (dailySale.amount > 0) {
                                            Text(
                                                text = "$${String.format("%.0f", dailySale.amount)}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFamily, fontSize = 7.sp),
                                                color = if (isMax) Green700 else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.75f)
                                                .height(barHeight)
                                                .background(
                                                    if (isMax) Green600 else GreenSoft,
                                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = dailySale.day,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
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
