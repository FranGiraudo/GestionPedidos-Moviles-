package com.undef.gestionpedidos.ui.feature.neworder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.ui.components.MoneyText
import com.undef.gestionpedidos.ui.theme.Green600
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewOrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_error)) },
            text = { Text(uiState.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(com.undef.gestionpedidos.R.string.txt_ok))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_nuevo_pedido), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Graphite900)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── TARJETA 1: Datos del cliente + observaciones ─────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_datos_del_cliente),
                        style = MaterialTheme.typography.titleMedium
                    )
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
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedClientMenu,
                            onDismissRequest = { viewModel.updateExpandedClientMenu(false) }
                        ) {
                            uiState.availableClients.forEach { cliente ->
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
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // ── TARJETA 2: Selección de productos (zona verde activa) ─────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_agregar_producto),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedProductMenu,
                            onDismissRequest = { viewModel.updateExpandedProductMenu(false) }
                        ) {
                            uiState.availableProducts.forEach { producto ->
                                DropdownMenuItem(
                                    text = { Text("${producto.descripcion} ($${producto.precioUnitario}) — Stock: ${producto.stockActual}") },
                                    onClick = { viewModel.updateSelectedProduct(producto) }
                                )
                            }
                        }
                    }

                    // Stepper de cantidad + Botón Agregar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Stepper − cantidad +
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val qty = (uiState.quantityText.toIntOrNull() ?: 1) - 1
                                    if (qty >= 1) viewModel.updateQuantityText(qty.toString())
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = uiState.quantityText,
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = MonoFamily),
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            IconButton(
                                onClick = {
                                    val qty = (uiState.quantityText.toIntOrNull() ?: 0) + 1
                                    viewModel.updateQuantityText(qty.toString())
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(18.dp))
                            }
                        }

                        // Botón agregar
                        Button(
                            onClick = { viewModel.addProduct() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            enabled = uiState.selectedProduct != null && (uiState.quantityText.toIntOrNull() ?: 0) > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Green600,
                                contentColor = Color.White,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(com.undef.gestionpedidos.R.string.txt_agregar))
                        }
                    }
                }
            }

            // ── TARJETA 3: Resumen del pedido ────────────────────────────────
            Text(
                text = stringResource(com.undef.gestionpedidos.R.string.txt_resumen_del_pedido),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.orderLines.isEmpty()) {
                // Empty state del carrito
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_aun_no_se_agregaron_),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        uiState.orderLines.forEachIndexed { index, linea ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = linea.producto.descripcion, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.W500)
                                    Text(
                                        text = "${linea.cantidad} × $${linea.precioUnitario}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = MonoFamily),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                MoneyText(
                                    text = "$${linea.subtotal}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W600,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(onClick = { viewModel.removeProduct(linea) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (index < uiState.orderLines.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // ── Barra de Total (grafito sólido) ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite900),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_total_a_pagar),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    MoneyText(
                        text = "$${uiState.total}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W700
                    )
                }
            }

            // ── Botón Confirmar (verde cuando hay productos, gris si no) ─────
            Button(
                onClick = { viewModel.saveOrder() },
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 16.dp),
                enabled = uiState.selectedClient != null && uiState.orderLines.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green600,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirmar y Guardar Pedido", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
