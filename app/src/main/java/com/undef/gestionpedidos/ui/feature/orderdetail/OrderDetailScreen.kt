package com.undef.gestionpedidos.ui.feature.orderdetail

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.R
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailViewModel = viewModel(
        factory = OrderDetailViewModelFactory(
            orderId,
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val order = uiState.order
    val context = LocalContext.current

    var modoEdicion by remember { mutableStateOf(false) }
    var numeroPedido by remember(order) { mutableStateOf(order?.numeroPedido ?: "") }
    var clienteRazonSocial by remember(order) { mutableStateOf(order?.cliente?.razonSocial ?: "") }
    var estadoSeleccionado by remember(order) { mutableStateOf(order?.estado ?: EstadoPedido.BORRADOR) }
    var total by remember(order) { mutableStateOf(order?.total ?: 0.0) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var dropdownExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OrderDetailTopBar(
                modoEdicion = modoEdicion,
                numeroPedido = numeroPedido,
                order = order,
                clienteRazonSocial = clienteRazonSocial,
                estadoSeleccionado = estadoSeleccionado,
                total = total,
                context = context,
                onNavigateBack = onNavigateBack,
                onToggleEdicion = { modoEdicion = it },
            )
        }
    ) { paddingValues ->
        if (order == null) {
            Text(
                text = stringResource(R.string.txt_pedido_no_encontrado),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OrderClientSection(
                modoEdicion = modoEdicion,
                clienteRazonSocial = clienteRazonSocial,
                onClienteChange = { clienteRazonSocial = it }
            )

            OrderEstadoSection(
                modoEdicion = modoEdicion,
                estadoSeleccionado = estadoSeleccionado,
                dropdownExpandido = dropdownExpandido,
                onDropdownExpandChange = { dropdownExpandido = it },
                onEstadoChange = { estadoSeleccionado = it }
            )

            if (modoEdicion) {
                OutlinedTextField(
                    value = numeroPedido,
                    onValueChange = { numeroPedido = it },
                    label = { Text(stringResource(R.string.txt_n_mero_de_pedido)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.txt_productos),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(order.lineas) { orderLine ->
                    OrderLineItem(orderLine)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OrderFooterSection(
                modoEdicion = modoEdicion,
                total = total,
                onTotalChange = { total = it.toDoubleOrNull() ?: 0.0 },
                onEliminarClick = { mostrarDialogoEliminar = true }
            )
        }
    }

    if (mostrarDialogoEliminar) {
        DeleteOrderDialog(
            numeroPedido = numeroPedido,
            onConfirm = {
                mostrarDialogoEliminar = false
                onNavigateBack()
            },
            onDismiss = { mostrarDialogoEliminar = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables extraídos
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailTopBar(
    modoEdicion: Boolean,
    numeroPedido: String,
    order: Pedido?,
    clienteRazonSocial: String,
    estadoSeleccionado: EstadoPedido,
    total: Double,
    context: android.content.Context,
    onNavigateBack: () -> Unit,
    onToggleEdicion: (Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            if (modoEdicion) {
                Text(stringResource(R.string.txt_editando_pedido))
            } else {
                Text(numeroPedido.ifEmpty { "Error" })
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (modoEdicion)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            IconButton(onClick = {
                if (modoEdicion) onToggleEdicion(false) else onNavigateBack()
            }) {
                Icon(
                    imageVector = if (modoEdicion) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = if (modoEdicion)
                        stringResource(R.string.txt_cancelar)
                    else
                        stringResource(R.string.cd_volver),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            if (order != null) {
                if (modoEdicion) {
                    IconButton(onClick = { onToggleEdicion(false) }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.cd_guardar_cambios),
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    IconButton(onClick = { onToggleEdicion(true) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_editar_pedido),
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = {
                        val mensaje = buildString {
                            appendLine("📋 $numeroPedido")
                            appendLine("🏢 Cliente: $clienteRazonSocial")
                            appendLine("📦 Estado: ${estadoSeleccionado.etiqueta}")
                            appendLine("💰 Total: $${String.format("%.2f", total)}")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, mensaje)
                            putExtra(Intent.EXTRA_SUBJECT, "$numeroPedido - $clienteRazonSocial")
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir pedido"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.cd_compartir_pedido),
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun OrderClientSection(
    modoEdicion: Boolean,
    clienteRazonSocial: String,
    onClienteChange: (String) -> Unit
) {
    if (modoEdicion) {
        OutlinedTextField(
            value = clienteRazonSocial,
            onValueChange = onClienteChange,
            label = { Text(stringResource(R.string.txt_cliente)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
    } else {
        Text(
            text = "Cliente: $clienteRazonSocial",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderEstadoSection(
    modoEdicion: Boolean,
    estadoSeleccionado: EstadoPedido,
    dropdownExpandido: Boolean,
    onDropdownExpandChange: (Boolean) -> Unit,
    onEstadoChange: (EstadoPedido) -> Unit
) {
    if (modoEdicion) {
        ExposedDropdownMenuBox(
            expanded = dropdownExpandido,
            onExpandedChange = onDropdownExpandChange
        ) {
            OutlinedTextField(
                value = estadoSeleccionado.etiqueta,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.txt_estado)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = dropdownExpandido,
                onDismissRequest = { onDropdownExpandChange(false) }
            ) {
                EstadoPedido.entries.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.etiqueta) },
                        onClick = {
                            onEstadoChange(estado)
                            onDropdownExpandChange(false)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    } else {
        Text(
            text = "Estado: ${estadoSeleccionado.etiqueta}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun OrderFooterSection(
    modoEdicion: Boolean,
    total: Double,
    onTotalChange: (String) -> Unit,
    onEliminarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (modoEdicion) {
            OutlinedTextField(
                value = total.toString(),
                onValueChange = onTotalChange,
                label = { Text(stringResource(R.string.txt_total)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        } else {
            Text(
                text = "Total: $${String.format("%.2f", total)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (!modoEdicion) {
            Button(
                onClick = onEliminarClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_eliminar)
                )
                Text(stringResource(R.string.txt_eliminar))
            }
        }
    }
}

@Composable
private fun DeleteOrderDialog(
    numeroPedido: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.txt_eliminar_pedido)) },
        text = {
            Text(stringResource(R.string.txt_confirm_delete_order, numeroPedido))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.txt_eliminar),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.txt_cancelar))
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Composable reutilizable para cada línea de pedido
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OrderLineItem(orderLine: LineaPedido) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = orderLine.producto.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${orderLine.cantidad} ${orderLine.producto.unidadMedida} x $${orderLine.precioUnitario}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "$${orderLine.subtotal}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
