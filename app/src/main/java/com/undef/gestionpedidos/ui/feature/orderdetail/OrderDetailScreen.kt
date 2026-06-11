package com.undef.gestionpedidos.ui.feature.orderdetail

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailViewModel = viewModel(factory = OrderDetailViewModelFactory(orderId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val order = uiState.order
    val pedidoOriginal = MockData.pedidos.find { it.id == orderId }
    val context = LocalContext.current

    // Estados para modo edición
    var modoEdicion by remember { mutableStateOf(false) }
    var numeroPedido by remember(pedidoOriginal) { mutableStateOf(pedidoOriginal?.numeroPedido ?: "") }
    var clienteRazonSocial by remember(pedidoOriginal) { mutableStateOf(pedidoOriginal?.cliente?.razonSocial ?: "") }
    var estadoSeleccionado by remember(pedidoOriginal) { mutableStateOf(pedidoOriginal?.estado ?: EstadoPedido.BORRADOR) }
    var total by remember(pedidoOriginal) { mutableStateOf(pedidoOriginal?.total ?: 0.0) }

    // Estados para diálogos
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var dropdownExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (modoEdicion) {
                        Text("Editando pedido")
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
                        if (modoEdicion) {
                            modoEdicion = false
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = if (modoEdicion) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (modoEdicion) "Cancelar" else "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    if (pedidoOriginal != null) {
                        if (modoEdicion) {
                            // Botón Guardar cambios
                            IconButton(onClick = {
                                modoEdicion = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Guardar cambios",
                                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            // Botón Editar
                            IconButton(onClick = { modoEdicion = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar pedido",
                                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            }
                            // Botón Compartir
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
                                    contentDescription = "Compartir pedido",
                                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (pedidoOriginal == null) {
            Text(
                text = "Pedido no encontrado",
                modifier = Modifier.padding(paddingValues).padding(16.dp),
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
            // Cliente
            if (modoEdicion) {
                OutlinedTextField(
                    value = clienteRazonSocial,
                    onValueChange = { clienteRazonSocial = it },
                    label = { Text("Cliente") },
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

            // Estado
            if (modoEdicion) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpandido,
                    onExpandedChange = { dropdownExpandido = it }
                ) {
                    OutlinedTextField(
                        value = estadoSeleccionado.etiqueta,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpandido,
                        onDismissRequest = { dropdownExpandido = false }
                    ) {
                        EstadoPedido.entries.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado.etiqueta) },
                                onClick = {
                                    estadoSeleccionado = estado
                                    dropdownExpandido = false
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

            // Número de pedido
            if (modoEdicion) {
                OutlinedTextField(
                    value = numeroPedido,
                    onValueChange = { numeroPedido = it },
                    label = { Text("Número de Pedido") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Productos",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(pedidoOriginal.lineas) { orderLine ->
                    OrderLineItem(orderLine)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total y botón Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (modoEdicion) {
                    OutlinedTextField(
                        value = total.toString(),
                        onValueChange = { nuevoValor ->
                            total = nuevoValor.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Total") },
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
                        onClick = { mostrarDialogoEliminar = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                        Text("Eliminar")
                    }
                }
            }
        }
    }

    // Diálogo de Eliminar
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Pedido") },
            text = {
                Text("¿Estás seguro de que deseas eliminar el pedido $numeroPedido?\n\nEsta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onNavigateBack()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun OrderLineItem(orderLine: LineaPedido) {
    Card(
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
