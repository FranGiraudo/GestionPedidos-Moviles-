package com.undef.gestionpedidos.ui.feature.neworder

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.R
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Producto
import com.undef.gestionpedidos.ui.theme.CardSurface

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewOrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
            if (uiState.pedidoEnviadoExitoso) {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            NewOrderTopBar(onNavigateBack = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClientDataCard(
                selectedClient = uiState.selectedClient,
                expandedClientMenu = uiState.expandedClientMenu,
                observaciones = uiState.observaciones,
                onExpandedChange = { viewModel.updateExpandedClientMenu(it) },
                onClientSelected = { viewModel.updateSelectedClient(it) },
                onObservacionesChange = { viewModel.updateObservaciones(it) }
            )

            AddProductCard(
                selectedProduct = uiState.selectedProduct,
                expandedProductMenu = uiState.expandedProductMenu,
                quantityText = uiState.quantityText,
                onExpandedChange = { viewModel.updateExpandedProductMenu(it) },
                onProductSelected = { viewModel.updateSelectedProduct(it) },
                onQuantityChange = { viewModel.updateQuantityText(it) },
                onAddProduct = { viewModel.addProduct() }
            )

            OrderSummarySection(
                orderLines = uiState.orderLines,
                total = uiState.total,
                onRemoveLine = { viewModel.removeProduct(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConfirmOrderButton(
                isLoading = uiState.isLoading,
                enabled = uiState.selectedClient != null
                        && uiState.orderLines.isNotEmpty()
                        && !uiState.isLoading,
                onClick = { viewModel.confirmarPedido() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables extraídos
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewOrderTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.txt_nuevo_pedido)) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_volver)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientDataCard(
    selectedClient: Cliente?,
    expandedClientMenu: Boolean,
    observaciones: String,
    onExpandedChange: (Boolean) -> Unit,
    onClientSelected: (Cliente) -> Unit,
    onObservacionesChange: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.txt_datos_del_cliente),
                style = MaterialTheme.typography.titleMedium
            )

            ExposedDropdownMenuBox(
                expanded = expandedClientMenu,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedClient?.razonSocial ?: stringResource(R.string.select_client),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.txt_cliente)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClientMenu)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedClientMenu,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    MockData.clientes.filter { it.activo }.forEach { cliente ->
                        DropdownMenuItem(
                            text = { Text(cliente.razonSocial) },
                            onClick = { onClientSelected(cliente) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = onObservacionesChange,
                label = { Text(stringResource(R.string.txt_observaciones_opcion)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductCard(
    selectedProduct: Producto?,
    expandedProductMenu: Boolean,
    quantityText: String,
    onExpandedChange: (Boolean) -> Unit,
    onProductSelected: (Producto) -> Unit,
    onQuantityChange: (String) -> Unit,
    onAddProduct: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.txt_agregar_producto),
                style = MaterialTheme.typography.titleMedium
            )

            ExposedDropdownMenuBox(
                expanded = expandedProductMenu,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedProduct?.descripcion ?: stringResource(R.string.select_product),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.txt_producto)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProductMenu)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedProductMenu,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    MockData.productos.filter { it.activo }.forEach { producto ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(
                                        R.string.txt_product_format,
                                        producto.descripcion,
                                        producto.precioUnitario
                                    )
                                )
                            },
                            onClick = { onProductSelected(producto) }
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
                    onValueChange = onQuantityChange,
                    label = { Text(stringResource(R.string.txt_cant)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.3f)
                )

                Button(
                    onClick = onAddProduct,
                    modifier = Modifier
                        .weight(0.7f)
                        .height(56.dp),
                    enabled = selectedProduct != null && (quantityText.toIntOrNull() ?: 0) > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.txt_agregar))
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(
    orderLines: List<LineaPedido>,
    total: Double,
    onRemoveLine: (LineaPedido) -> Unit
) {
    Text(
        text = stringResource(R.string.txt_resumen_del_pedido),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )

    if (orderLines.isEmpty()) {
        Text(
            text = stringResource(R.string.txt_aun_no_se_agregaron_),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        OrderLinesCard(orderLines = orderLines, onRemoveLine = onRemoveLine)
    }

    OrderTotalCard(total = total)
}

@Composable
private fun OrderLinesCard(
    orderLines: List<LineaPedido>,
    onRemoveLine: (LineaPedido) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            orderLines.forEachIndexed { index, linea ->
                OrderLineRow(linea = linea, onRemove = { onRemoveLine(linea) })
                if (index < orderLines.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderLineRow(linea: LineaPedido, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = linea.producto.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
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
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.cd_eliminar),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun OrderTotalCard(total: Double) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.txt_total_a_pagar),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$${total}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ConfirmOrderButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.confirm_order_button),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
