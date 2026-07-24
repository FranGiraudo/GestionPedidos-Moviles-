package com.undef.gestionpedidos.ui.feature.newproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.ui.theme.Green600
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProductScreen(
    productId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: NewProductViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var categoriaMenuExpanded by remember { mutableStateOf(false) }
    val isEditing = productId != null && productId > 0

    LaunchedEffect(productId) {
        if (isEditing) viewModel.loadProduct(productId!!)
    }

    val isFormValid = uiState.codigo.isNotBlank()
        && uiState.descripcion.isNotBlank()
        && (uiState.precioUnitario.toDoubleOrNull() ?: 0.0) > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Producto"
                               else stringResource(com.undef.gestionpedidos.R.string.txt_nuevo_producto),
                        color = Color.White
                    )
                },
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
            Spacer(modifier = Modifier.height(8.dp))

            // ── Error banner ──────────────────────────────────────────────────
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // ── CARD 1: Datos del producto ────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Datos del producto",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Descripción (nombre principal — más prominente)
                    OutlinedTextField(
                        value = uiState.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_descripci_n)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleMedium,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Código — monoespaciado en el campo
                    OutlinedTextField(
                        value = uiState.codigo,
                        onValueChange = viewModel::onCodigoChange,
                        label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_c_digo_ej_p001)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonoFamily),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        supportingText = { Text("Ej: P001", style = MaterialTheme.typography.labelSmall) }
                    )

                    // Categoría (si hay disponibles)
                    if (uiState.categorias.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = categoriaMenuExpanded,
                            onExpandedChange = { categoriaMenuExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = uiState.categorias.find { it.id == uiState.categoriaSeleccionadaId }?.nombre
                                    ?: stringResource(com.undef.gestionpedidos.R.string.txt_categor_a),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_categor_a)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaMenuExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoriaMenuExpanded,
                                onDismissRequest = { categoriaMenuExpanded = false }
                            ) {
                                uiState.categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria.nombre) },
                                        onClick = {
                                            viewModel.onCategoriaSelected(categoria.id)
                                            categoriaMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Unidad de medida
                    OutlinedTextField(
                        value = uiState.unidadMedida,
                        onValueChange = viewModel::onUnidadMedidaChange,
                        label = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_unidad_ej_kg_lt_un)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // ── CARD 2: Precio y stock ────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Precio y stock",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Precio con prefijo "$" fijo
                    OutlinedTextField(
                        value = uiState.precioUnitario,
                        onValueChange = viewModel::onPrecioUnitarioChange,
                        label = { Text("Precio unitario") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = MonoFamily,
                            fontWeight = FontWeight.W600
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Stock — stepper − [cantidad] + (mismo componente que NewOrderScreen)
                    Column {
                        Text(
                            text = stringResource(com.undef.gestionpedidos.R.string.txt_stock_inicial_opcion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Stepper
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                    .border(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val qty = (uiState.stockActual.toIntOrNull() ?: 0) - 1
                                        if (qty >= 0) viewModel.onStockActualChange(qty.toString())
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Menos",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = uiState.stockActual.ifBlank { "0" },
                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = MonoFamily),
                                    fontWeight = FontWeight.W600,
                                    modifier = Modifier
                                        .width(52.dp)
                                        .padding(horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                IconButton(
                                    onClick = {
                                        val qty = (uiState.stockActual.toIntOrNull() ?: 0) + 1
                                        viewModel.onStockActualChange(qty.toString())
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Más",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Campo de texto alternativo para escribir directamente
                            OutlinedTextField(
                                value = uiState.stockActual,
                                onValueChange = viewModel::onStockActualChange,
                                label = { Text("O escribí el valor") },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonoFamily),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // ── Botón guardar ─────────────────────────────────────────────────
            Button(
                onClick = { viewModel.saveProduct(onSuccess = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                enabled = isFormValid && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green600,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (uiState.isLoading) "Guardando..."
                           else stringResource(com.undef.gestionpedidos.R.string.txt_guardar_producto),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
