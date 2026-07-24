package com.undef.gestionpedidos.ui.feature.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.ui.components.EmptyState
import com.undef.gestionpedidos.ui.components.FilterChipRow
import com.undef.gestionpedidos.ui.components.OrderHistoryCard
import com.undef.gestionpedidos.ui.theme.Graphite900
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// null = "Todos"
private typealias EstadoFilter = EstadoPedido?

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigateToNewOrder: () -> Unit,
    onNavigateToOrderDetail: (Int) -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf<EstadoFilter>(null) }

    val filterOptions: List<Pair<EstadoFilter, String>> = listOf(
        null                        to "Todos",
        EstadoPedido.BORRADOR       to "Borrador",
        EstadoPedido.EN_PREPARACION to "En prep.",
        EstadoPedido.DESPACHADO     to "Despachado",
        EstadoPedido.ENTREGADO      to "Entregado",
        EstadoPedido.CANCELADO      to "Cancelado"
    )

    val filtered = uiState.orders.let { list ->
        if (activeFilter == null) list else list.filter { it.estado == activeFilter }
    }

    // Agrupar por fecha
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val grouped: Map<String, List<Pedido>> = filtered
        .sortedByDescending { it.fechaCreacion }
        .groupBy { pedido ->
            when (pedido.fechaCreacion) {
                today     -> "Hoy"
                yesterday -> "Ayer"
                else      -> pedido.fechaCreacion.format(
                    DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
                ).replaceFirstChar { it.uppercase() }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_historial_de_pedidos)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite900,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewOrder,
                containerColor = Graphite900,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo Pedido") }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Chips de filtro por estado
            FilterChipRow(
                options = filterOptions,
                selected = activeFilter,
                onSelect = { activeFilter = it },
                modifier = Modifier.padding(vertical = 10.dp)
            )

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ShoppingCart,
                    title = "Sin pedidos",
                    subtitle = when {
                        activeFilter != null -> "No hay pedidos en estado \"${activeFilter!!.etiqueta}\""
                        else                 -> "Toca + para crear tu primer pedido"
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grouped.forEach { (dateLabel, orders) ->
                        // Header de sección de fecha
                        item(key = "header_$dateLabel") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.W600,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        // Cards del grupo
                        items(orders, key = { it.id }) { order ->
                            OrderHistoryCard(
                                order = order,
                                onClick = { onNavigateToOrderDetail(order.id) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
