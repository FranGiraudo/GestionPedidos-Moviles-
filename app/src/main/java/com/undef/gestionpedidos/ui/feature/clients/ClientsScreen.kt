package com.undef.gestionpedidos.ui.feature.clients

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.ui.components.AvatarLetter
import com.undef.gestionpedidos.ui.components.EmptyState
import com.undef.gestionpedidos.ui.components.FilterChipRow
import com.undef.gestionpedidos.ui.components.ListSearchBar
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily

private enum class ClientFilter { TODOS, ACTIVOS, INACTIVOS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    onNavigateToNewClient: () -> Unit,
    onNavigateToEditClient: (Int) -> Unit,
    viewModel: ClientsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf(ClientFilter.TODOS) }

    val displayed = uiState.clients.filter { c ->
        when (activeFilter) {
            ClientFilter.TODOS     -> true
            ClientFilter.ACTIVOS   -> c.activo
            ClientFilter.INACTIVOS -> !c.activo
        }
    }

    val filterOptions = listOf(
        ClientFilter.TODOS     to "Todos",
        ClientFilter.ACTIVOS   to "Activos",
        ClientFilter.INACTIVOS to "Inactivos"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(com.undef.gestionpedidos.R.string.txt_gestion_de_clientes)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite900,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewClient,
                containerColor = Graphite900,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo Cliente") }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Barra de búsqueda
            ListSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "Buscar por nombre o CUIT...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
            // Chips de filtro
            FilterChipRow(
                options = filterOptions,
                selected = activeFilter,
                onSelect = { activeFilter = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (displayed.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Group,
                    title = "Sin clientes",
                    subtitle = if (uiState.searchQuery.isNotBlank()) "No hay resultados para \"${uiState.searchQuery}\""
                               else "Toca + para agregar tu primer cliente",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayed, key = { it.id }) { client ->
                        ClientCard(
                            client = client,
                            onClick = { onNavigateToEditClient(client.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCard(client: Cliente, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    val isActive = client.activo

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (isActive) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // ── Fila superior: avatar | nombre | CUIT ──────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarLetter(
                    name = client.razonSocial,
                    size = 42.dp,
                    bgColor = if (isActive) Graphite900
                              else MaterialTheme.colorScheme.surfaceVariant,
                    textColor = if (isActive) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client.razonSocial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W500,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (client.localidad.isNotBlank()) {
                        Text(
                            text = client.localidad,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // CUIT monoespaciado alineado a la derecha
                Text(
                    text = client.cuit,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = MonoFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            // ── Fila inferior: chips contacto + acciones rápidas ────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Chips de contacto
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (client.telefono.isNotBlank()) {
                        ContactChip(
                            icon = Icons.Default.Call,
                            label = client.telefono
                        )
                    }
                    if (client.email.isNotBlank()) {
                        ContactChip(
                            icon = Icons.Default.Email,
                            label = client.email.substringBefore("@")
                        )
                    }
                }
                // Acciones rápidas
                Row {
                    if (client.telefono.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.telefono}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Llamar",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (client.email.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${client.email}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Badge inactivo
            if (!isActive) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = stringResource(com.undef.gestionpedidos.R.string.txt_inactivo),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
