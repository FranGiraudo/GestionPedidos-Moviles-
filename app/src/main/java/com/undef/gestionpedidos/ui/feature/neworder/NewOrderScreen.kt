package com.undef.gestionpedidos.ui.feature.neworder

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
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.data.mock.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewOrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_nuevo_pedido)) },
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
                    Text(text = stringResource(com.undef.gestionpedidos.R.string.txt_datos_del_cliente), style = MaterialTheme.typography.titleMedium)

                    ExposedDropdownMenuBox(
                        expanded = uiState.expandedClientMenu,
                        onExpandedChange = { viewModel.updateExpandedClientMenu(it) }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedClient?.razonSocial ?: "Seleccione un cliente",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_cliente)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedClientMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedClientMenu,
                            onDismissRequest = { viewModel.updateExpandedClientMenu(false) }
                        ) {
                            MockData.clientes.filter { it.activo }.forEach { cliente ->
                                DropdownMenuItem(
                                    text = { Text(cliente.razonSocial) },
                                    onClick = { viewModel.updateSelectedClient(cliente) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.observaciones,
                        onValueChange = { viewModel.updateObservaciones(it) },
                        label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_observaciones_opcion)) },
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
                    Text(text = stringResource(com.undef.gestionpedidos.R.string.txt_agregar_producto), style = MaterialTheme.typography.titleMedium)

                    ExposedDropdownMenuBox(
                        expanded = uiState.expandedProductMenu,
                        onExpandedChange = { viewModel.updateExpandedProductMenu(it) }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedProduct?.descripcion ?: "Seleccione un producto",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_producto)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedProductMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedProductMenu,
                            onDismissRequest = { viewModel.updateExpandedProductMenu(false) }
                        ) {
                            MockData.productos.filter { it.activo }.forEach { producto ->
                                DropdownMenuItem(
                                    text = { Text("${producto.descripcion} ($${producto.precioUnitario})") },
                                    onClick = { viewModel.updateSelectedProduct(producto) }
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
                            value = uiState.quantityText,
                            onValueChange = { viewModel.updateQuantityText(it) },
                            label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_cant)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.3f)
                        )

                        Button(
                            onClick = { viewModel.addProduct() },
                            modifier = Modifier
                                .weight(0.7f)
                                .height(56.dp),
                            enabled = uiState.selectedProduct != null && (uiState.quantityText.toIntOrNull() ?: 0) > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(com.undef.gestionpedidos.R.string.txt_agregar))
                        }
                    }
                }
            }

            // SECCION 3: Resumen del Pedido
            Text(
                text = stringResource(com.undef.gestionpedidos.R.string.txt_resumen_del_pedido),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (uiState.orderLines.isEmpty()) {
                Text(
                    text = stringResource(com.undef.gestionpedidos.R.string.txt_aun_no_se_agregaron_),
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
                        uiState.orderLines.forEachIndexed { index, linea ->
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
                                    onClick = { viewModel.removeProduct(linea) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            if (index < uiState.orderLines.lastIndex) {
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
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_total_a_pagar),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$${uiState.total}",
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
                enabled = uiState.selectedClient != null && uiState.orderLines.isNotEmpty()
            ) {
                Text("Confirmar y Guardar Pedido", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
