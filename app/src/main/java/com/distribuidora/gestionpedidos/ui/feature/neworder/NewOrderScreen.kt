package com.distribuidora.gestionpedidos.ui.feature.neworder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.distribuidora.gestionpedidos.data.mock.MockData
import com.distribuidora.gestionpedidos.domain.model.Cliente
import com.distribuidora.gestionpedidos.domain.model.LineaPedido
import com.distribuidora.gestionpedidos.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit
) {
    // Estados del cliente y generales
    var expandedClientMenu by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf<Cliente?>(null) }
    var observaciones by remember { mutableStateOf("") }

    // Estados del selector de productos
    var expandedProductMenu by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Producto?>(null) }
    var quantityText by remember { mutableStateOf("1") }

    // Lista interactiva de lineas de pedido
    val orderLines = remember { mutableStateListOf<LineaPedido>() }
    val total = orderLines.sumOf { it.subtotal }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Pedido") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TARJETA 1: Datos Generales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Datos del Cliente", style = MaterialTheme.typography.titleMedium)

                    ExposedDropdownMenuBox(
                        expanded = expandedClientMenu,
                        onExpandedChange = { expandedClientMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedClient?.razonSocial ?: "Seleccione un cliente",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cliente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClientMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedClientMenu,
                            onDismissRequest = { expandedClientMenu = false }
                        ) {
                            MockData.clientes.filter { it.activo }.forEach { cliente ->
                                DropdownMenuItem(
                                    text = { Text(cliente.razonSocial) },
                                    onClick = {
                                        selectedClient = cliente
                                        expandedClientMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        label = { Text("Observaciones (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            // TARJETA 2: Seleccion de Productos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Agregar Producto", style = MaterialTheme.typography.titleMedium)

                    ExposedDropdownMenuBox(
                        expanded = expandedProductMenu,
                        onExpandedChange = { expandedProductMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedProduct?.descripcion ?: "Seleccione un producto",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Producto") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProductMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProductMenu,
                            onDismissRequest = { expandedProductMenu = false }
                        ) {
                            MockData.productos.filter { it.activo }.forEach { producto ->
                                DropdownMenuItem(
                                    text = { Text("${producto.descripcion} ($${producto.precioUnitario})") },
                                    onClick = {
                                        selectedProduct = producto
                                        expandedProductMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                    quantityText = newValue
                                }
                            },
                            label = { Text("Cant.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.3f)
                        )

                        Button(
                            onClick = {
                                val qty = quantityText.toIntOrNull() ?: 0
                                val product = selectedProduct

                                if (product != null && qty > 0) {
                                    // Verificamos si el producto ya esta en la lista para sumar cantidad
                                    val existingIndex = orderLines.indexOfFirst { it.producto.id == product.id }
                                    if (existingIndex != -1) {
                                        val existingLine = orderLines[existingIndex]
                                        orderLines[existingIndex] = existingLine.copy(
                                            cantidad = existingLine.cantidad + qty
                                        )
                                    } else {
                                        orderLines.add(
                                            LineaPedido(
                                                id = orderLines.size + 1,
                                                pedidoId = 0,
                                                producto = product,
                                                cantidad = qty,
                                                precioUnitario = product.precioUnitario
                                            )
                                        )
                                    }
                                    // Limpiamos los campos luego de agregar
                                    selectedProduct = null
                                    quantityText = "1"
                                }
                            },
                            modifier = Modifier
                                .weight(0.7f)
                                .height(56.dp),
                            enabled = selectedProduct != null && (quantityText.toIntOrNull() ?: 0) > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar")
                        }
                    }
                }
            }

            // SECCION 3: Resumen del Pedido
            Text(
                text = "Resumen del Pedido",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (orderLines.isEmpty()) {
                Text(
                    text = "Aun no se agregaron productos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        orderLines.forEachIndexed { index, linea ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = linea.producto.descripcion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${linea.cantidad} ${linea.producto.unidadMedida} x $${linea.precioUnitario}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$${linea.subtotal}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { orderLines.remove(linea) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            if (index < orderLines.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
            }

            // Total y Confirmacion
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total a pagar:",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$$total",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // TODO: Guardar pedido en la base de datos local (Room)
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedClient != null && orderLines.isNotEmpty()
            ) {
                Text("Confirmar y Guardar Pedido", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}