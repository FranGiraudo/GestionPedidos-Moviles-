package com.undef.gestionpedidos.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.ui.theme.Graphite900
import com.undef.gestionpedidos.ui.theme.MonoFamily
import com.undef.gestionpedidos.ui.theme.StatusGreenBg
import com.undef.gestionpedidos.ui.theme.StatusGreenText
import com.undef.gestionpedidos.ui.theme.StatusNeutralBg
import com.undef.gestionpedidos.ui.theme.StatusNeutralText
import com.undef.gestionpedidos.ui.theme.StatusOrangeBg
import com.undef.gestionpedidos.ui.theme.StatusOrangeText
import java.time.format.DateTimeFormatter

// ─── MoneyText ────────────────────────────────────────────────────────────────
/** Texto de monto/stock en fuente monoespaciada. Usado para valores numéricos. */
@Composable
fun MoneyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.W600
) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = MonoFamily,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// ─── StatusPill ───────────────────────────────────────────────────────────────
@Composable
fun StatusPill(status: EstadoPedido, modifier: Modifier = Modifier) {
    val bg = when (status) {
        EstadoPedido.ENTREGADO     -> StatusGreenBg
        EstadoPedido.EN_PREPARACION,
        EstadoPedido.DESPACHADO    -> StatusOrangeBg
        EstadoPedido.CANCELADO     -> MaterialTheme.colorScheme.errorContainer
        else                       -> StatusNeutralBg
    }
    val fg = when (status) {
        EstadoPedido.ENTREGADO     -> StatusGreenText
        EstadoPedido.EN_PREPARACION,
        EstadoPedido.DESPACHADO    -> StatusOrangeText
        EstadoPedido.CANCELADO     -> MaterialTheme.colorScheme.onErrorContainer
        else                       -> StatusNeutralText
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = status.etiqueta,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ─── AvatarLetter ─────────────────────────────────────────────────────────────
@Composable
fun AvatarLetter(
    name: String,
    size: Dp = 44.dp,
    bgColor: Color = Graphite900,
    textColor: Color = Color.White
) {
    val letter = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = textColor,
            fontWeight = FontWeight.W600,
            fontSize = (size.value * 0.38f).sp
        )
    }
}

// ─── SummaryCard (KPI) ───────────────────────────────────────────────────────
@Composable
fun SummaryCard(
    title: String,
    value: String,
    bgColor: Color,
    fgColor: Color,
    elevation: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = fgColor.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.weight(1f))
            MoneyText(
                text = value,
                color = fgColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.W700
            )
        }
    }
}

// ─── RecentOrderCard ─────────────────────────────────────────────────────────
@Composable
fun RecentOrderCard(order: Pedido, onClick: () -> Unit) {
    val avatarBg = when (order.estado) {
        EstadoPedido.ENTREGADO  -> StatusGreenBg
        EstadoPedido.CANCELADO  -> MaterialTheme.colorScheme.errorContainer
        EstadoPedido.DESPACHADO,
        EstadoPedido.EN_PREPARACION -> StatusOrangeBg
        else                    -> MaterialTheme.colorScheme.surfaceVariant
    }
    val avatarFg = when (order.estado) {
        EstadoPedido.ENTREGADO  -> StatusGreenText
        EstadoPedido.CANCELADO  -> MaterialTheme.colorScheme.onErrorContainer
        EstadoPedido.DESPACHADO,
        EstadoPedido.EN_PREPARACION -> StatusOrangeText
        else                    -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarLetter(name = order.cliente.razonSocial, bgColor = avatarBg, textColor = avatarFg)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.cliente.razonSocial,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusPill(status = order.estado)
            }
            Spacer(modifier = Modifier.width(8.dp))
            MoneyText(
                text = "$${String.format("%.0f", order.total)}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600
            )
        }
    }
}

// ─── OrderHistoryCard ─────────────────────────────────────────────────────────
@Composable
fun OrderHistoryCard(order: Pedido, onClick: () -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoneyText(
                    text = order.numeroPedido,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = order.fechaCreacion.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = order.cliente.razonSocial,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(status = order.estado)
                MoneyText(
                    text = "$${String.format("%.2f", order.total)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

// ─── ProfileInfoRow ──────────────────────────────────────────────────────────
@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─── EmptyState ───────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(20.dp))
            action()
        }
    }
}

// ─── SkeletonCard (Shimmer) ──────────────────────────────────────────────────
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceVariant
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.55f).height(13.dp).background(brush, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(0.35f).height(10.dp).background(brush, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.width(64.dp).height(22.dp).background(brush, RoundedCornerShape(20.dp)))
                Box(modifier = Modifier.width(80.dp).height(16.dp).background(brush, RoundedCornerShape(4.dp)))
            }
        }
    }
}

// ─── SearchBar ────────────────────────────────────────────────────────────────
/** Barra de búsqueda reutilizable para listas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Limpiar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp).clickable { onQueryChange("") }
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

// ─── FilterChipRow ────────────────────────────────────────────────────────────
/** Fila horizontal de chips de filtro rápido. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterChipRow(
    options: List<Pair<T, String>>,          // valor → etiqueta
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(options) { (value, label) ->
            val isSelected = value == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(value) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = MaterialTheme.colorScheme.secondary,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    borderWidth = 0.5.dp,
                    selectedBorderWidth = 0.dp
                )
            )
        }
    }
}

// ─── ProfileInfoRow ───────────────────────────────────────────────────────────
/** Fila label/valor para la sección de perfil. */
@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.65f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

// ─── OrderHistoryCard ─────────────────────────────────────────────────────────
/** Card de pedido para el historial agrupado por fecha. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryCard(
    order: Pedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "# ${order.numeroPedido}",
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = MonoFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.cliente.razonSocial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                StatusPill(status = order.estado)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$ ${String.format("%.2f", order.total)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = MonoFamily),
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

