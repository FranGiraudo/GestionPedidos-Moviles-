package com.undef.gestionpedidos.ui.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.undef.gestionpedidos.ui.components.EmptyState
import com.undef.gestionpedidos.ui.components.MoneyText
import com.undef.gestionpedidos.ui.components.RecentOrderCard
import com.undef.gestionpedidos.ui.components.SkeletonCard
import com.undef.gestionpedidos.ui.components.SummaryCard
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.Green600

@Composable
fun DashboardScreen(
    onNavigateToNewOrder: () -> Unit,
    onNavigateToNewClient: () -> Unit,
    onNavigateToNewProduct: () -> Unit,
    onNavigateToOrderDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentOrders = uiState.pedidosRecientes.take(5)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header grafito ────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Graphite900)
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 48.dp) // bottom extra para overlap de cards
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (uiState.userName.isNotBlank()) "Hola, ${uiState.userName}" else "Hola 👋",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(com.undef.gestionpedidos.R.string.txt_resumen_del_dia),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    // Avatar del usuario
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Green600,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // ── KPI cards flotantes ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Ventas totales",
                    value = "$ ${uiState.totalVentas}",
                    bgColor = MaterialTheme.colorScheme.secondary,
                    fgColor = Color.White,
                    elevation = 8.dp,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Dólar Blue",
                    value = "$ ${uiState.dolarBlue}",
                    bgColor = MaterialTheme.colorScheme.surface,
                    fgColor = MaterialTheme.colorScheme.onSurface,
                    elevation = 8.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Acciones rápidas ─────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-8).dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botón primario: Nuevo Pedido (verde)
                FilledTonalButton(
                    onClick = onNavigateToNewOrder,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Green600,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pedido", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                }
                // Secundario: Nuevo Cliente
                FilledTonalButton(
                    onClick = onNavigateToNewClient,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cliente", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                }
                // Secundario: Nuevo Producto
                FilledTonalButton(
                    onClick = onNavigateToNewProduct,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prod.", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                }
            }
        }

        // ── Sección últimos movimientos ──────────────────────────────────────
        item {
            Text(
                text = stringResource(com.undef.gestionpedidos.R.string.txt_ultimos_movimientos),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 12.dp)
            )
        }

        if (uiState.isLoading) {
            items(3) { SkeletonCard(modifier = Modifier.padding(horizontal = 20.dp)) }
        } else if (recentOrders.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.ShoppingCart,
                    title = "Sin movimientos aún",
                    subtitle = "Tus últimos pedidos aparecerán aquí",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        } else {
            items(recentOrders) { order ->
                RecentOrderCard(
                    order = order,
                    onClick = { onNavigateToOrderDetail(order.id) },
                )
                Spacer(modifier = Modifier.height(8.dp).padding(horizontal = 20.dp))
            }
        }
    }
}
