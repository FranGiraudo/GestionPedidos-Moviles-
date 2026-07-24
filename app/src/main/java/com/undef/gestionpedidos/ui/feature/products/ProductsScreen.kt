package com.undef.gestionpedidos.ui.feature.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.domain.model.Producto
import com.undef.gestionpedidos.ui.components.EmptyState
import com.undef.gestionpedidos.ui.components.FilterChipRow
import com.undef.gestionpedidos.ui.components.ListSearchBar
import com.undef.gestionpedidos.ui.components.MoneyText
import com.undef.gestionpedidos.ui.theme.Green600
import com.undef.gestionpedidos.ui.theme.Green700
import com.undef.gestionpedidos.ui.theme.GreenSoft
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily
import com.undef.gestionpedidos.ui.theme.StatusNeutralBg
import com.undef.gestionpedidos.ui.theme.StatusNeutralText

private enum class ProductFilter { TODOS, STOCK_BAJO, INACTIVOS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onNavigateToNewProduct: () -> Unit,
    onNavigateToEditProduct: (Int) -> Unit,
    viewModel: ProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf(ProductFilter.TODOS) }

    val STOCK_BAJO_UMBRAL = 5

    val displayed = uiState.products.filter { p ->
        when (activeFilter) {
            ProductFilter.TODOS      -> true
            ProductFilter.STOCK_BAJO -> p.activo && p.stockActual in 1..STOCK_BAJO_UMBRAL
            ProductFilter.INACTIVOS  -> !p.activo
        }
    }

    val filterOptions = listOf(
        ProductFilter.TODOS      to "Todos",
        ProductFilter.STOCK_BAJO to "Stock bajo",
        ProductFilter.INACTIVOS  to "Inactivos"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_productos)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite900,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewProduct,
                containerColor = Graphite900,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo Producto") }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ListSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "Buscar por nombre o código...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
            FilterChipRow(
                options = filterOptions,
                selected = activeFilter,
                onSelect = { activeFilter = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (displayed.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inventory,
                    title = "Sin productos",
                    subtitle = if (uiState.searchQuery.isNotBlank()) "No hay resultados para \"${uiState.searchQuery}\""
                               else when (activeFilter) {
                                   ProductFilter.STOCK_BAJO -> "No hay productos con stock bajo"
                                   ProductFilter.INACTIVOS  -> "No hay productos inactivos"
                                   else                     -> "Toca + para agregar tu primer producto"
                               },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayed, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            stockBajoUmbral = STOCK_BAJO_UMBRAL,
                            onClick = { onNavigateToEditProduct(product.id) },
                            onToggleActive = {
                                if (product.activo) viewModel.deactivateProduct(product.id)
                                else viewModel.activateProduct(product.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Producto,
    stockBajoUmbral: Int = 5,
    onClick: () -> Unit,
    onToggleActive: () -> Unit
) {
    val isActive = product.activo
    val cardAlpha = if (isActive) 1f else 0.6f

    val (stockBg, stockFg, stockLabel) = when {
        !isActive                         -> Triple(StatusNeutralBg, StatusNeutralText, "Inactivo")
        product.stockActual <= 0          -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Sin stock")
        product.stockActual <= stockBajoUmbral -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Stock bajo: ${product.stockActual}")
        else                              -> Triple(GreenSoft, Green700, "Stock: ${product.stockActual}")
    }

    Card(
        modifier = Modifier.fillMaxWidth().alpha(cardAlpha),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else StatusNeutralBg.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isActive) 1f else 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder cuadrado con iniciales
            Surface(
                color = if (isActive) Graphite900 else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(50.dp)
            ) {
                androidx.compose.foundation.layout.Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.descripcion.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.W700,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Nombre + código
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = product.codigo,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = MonoFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Stock pill semántico
                Surface(color = stockBg, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = stockLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFamily),
                        color = stockFg,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Precio + toggle
            Column(horizontalAlignment = Alignment.End) {
                MoneyText(
                    text = "$${product.precioUnitario}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700
                )
                Text(
                    text = "/${product.unidadMedida}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = onToggleActive, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Visibility else Icons.Default.Refresh,
                        contentDescription = if (isActive) "Desactivar" else "Reactivar",
                        tint = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant
                               else Green600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
