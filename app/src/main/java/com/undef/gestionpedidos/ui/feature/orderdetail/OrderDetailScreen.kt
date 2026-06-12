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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    
    val context = LocalContext.current

    // Estados para modo edición
    var modoEdicion by remember { mutableStateOf(false) }
    var numeroPedido by remember(order) { mutableStateOf(order?.numeroPedido ?: "") }
    var clienteRazonSocial by remember(order) { mutableStateOf(order?.cliente?.razonSocial ?: "") }
    var estadoSeleccionado by remember(order) { mutableStateOf(order?.estado ?: EstadoPedido.BORRADOR) }
    val totalCalculado = uiState.editableLines.sumOf { it.subtotal }

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var dropdownExpandido by remember { mutableStateOf(false) }
    var mostrarImagenCompleta by remember { mutableStateOf(false) }

    // Launcher de galería
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.attachComprobante(uri.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (modoEdicion) {
                        Text(stringResource(com.undef.gestionpedidos.R.string.txt_editando_pedido))
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
                    if (order != null) {
                        if (!modoEdicion) {
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
                                    appendLine("💰 Total: $${String.format("%.2f", totalCalculado)}")
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
        if (order == null) {
            Text(
                text = stringResource(com.undef.gestionpedidos.R.string.txt_pedido_no_encontrado),
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
                    label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_cliente)) },
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
                        label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_estado)) },
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
                    label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_n_mero_de_pedido)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comprobante
            Text(
                text = "Comprobante",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (order.comprobanteUri != null) {
                AsyncImage(
                    model = order.comprobanteUri,
                    contentDescription = "Comprobante del pedido",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clickable { mostrarImagenCompleta = true },
                    contentScale = ContentScale.Crop
                )
                if (modoEdicion) {
                    TextButton(onClick = { 
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                    }) {
                        Text("Cambiar Comprobante")
                    }
                }
            } else {
                if (modoEdicion) {
                    Button(
                        onClick = { 
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adjuntar Comprobante")
                    }
                } else {
                    Text("Sin comprobante adjunto", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(com.undef.gestionpedidos.R.string.txt_productos),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.editableLines) { orderLine ->
                    OrderLineItem(
                        orderLine = orderLine,
                        modoEdicion = modoEdicion,
                        onQuantityChanged = { newQty -> viewModel.updateLineQuantity(orderLine.producto.id, newQty) },
                        onDelete = { viewModel.removeLine(orderLine.producto.id) }
                    )
                }
                
                if (modoEdicion && uiState.availableProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        var showAddDropdown by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = showAddDropdown,
                            onExpandedChange = { showAddDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = "Agregar Producto",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAddDropdown) },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = showAddDropdown,
                                onDismissRequest = { showAddDropdown = false }
                            ) {
                                uiState.availableProducts.forEach { product ->
                                    DropdownMenuItem(
                                        text = { Text("${product.descripcion} - $${product.precioUnitario}") },
                                        onClick = {
                                            viewModel.addProductLine(product)
                                            showAddDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total y botón Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total: $${String.format("%.2f", totalCalculado)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

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
                        Text(stringResource(com.undef.gestionpedidos.R.string.txt_eliminar))
                    }
                }
            }

            if (modoEdicion) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.saveOrderChanges(estadoSeleccionado)
                        modoEdicion = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar y Guardar Cambios", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    // Diálogo de Eliminar
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_eliminar_pedido)) },
            text = {
                Text("¿Estás seguro de que deseas eliminar el pedido $numeroPedido?\n\nEsta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        viewModel.deleteOrder()
                        onNavigateBack()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text(stringResource(com.undef.gestionpedidos.R.string.txt_cancelar))
                }
            }
        )
    }

    // Diálogo de Imagen Completa
    if (mostrarImagenCompleta && order?.comprobanteUri != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { mostrarImagenCompleta = false }) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = order.comprobanteUri,
                    contentDescription = "Comprobante en pantalla completa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { mostrarImagenCompleta = false },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}

@Composable
fun OrderLineItem(
    orderLine: LineaPedido,
    modoEdicion: Boolean,
    onQuantityChanged: (Int) -> Unit,
    onDelete: () -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = orderLine.producto.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (modoEdicion) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        IconButton(
                            onClick = { onQuantityChanged(orderLine.cantidad - 1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("-", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            text = "${orderLine.cantidad} ${orderLine.producto.unidadMedida}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { onQuantityChanged(orderLine.cantidad + 1) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("+", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                } else {
                    Text(
                        text = "${orderLine.cantidad} ${orderLine.producto.unidadMedida} x $${orderLine.precioUnitario}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${orderLine.subtotal}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (modoEdicion) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
