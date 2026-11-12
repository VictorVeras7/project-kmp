package org.example.project.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.OrderStatus
import org.example.project.data.OrderSummary
import org.example.project.data.SampleOrderHistory
import org.example.project.data.formatMoney
import org.example.project.data.itemsLabel
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

private enum class OrdersTab(val label: String, val emptyTitle: String) {
    Live("Em andamento", "Nenhum pedido em andamento"),
    Past("Anteriores", "Nenhum pedido anterior"),
}

@Composable
fun OrdersScreen(
    liveOrders: List<OrderSummary>,
    pastOrders: List<OrderSummary>,
    loading: Boolean,
    onOpenOrder: (Int) -> Unit,
    onTrackOrder: (Int) -> Unit,
    onReorder: (Int) -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by rememberSaveable { mutableStateOf(OrdersTab.Live) }
    val orders = if (tab == OrdersTab.Live) liveOrders else pastOrders

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            OrdersHeader(selected = tab, onSelect = { tab = it })
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                when {
                    orders.isNotEmpty() -> orders.forEach { order ->
                        OrderCard(
                            order = order,
                            onOpen = { onOpenOrder(order.id) },
                            onTrack = { onTrackOrder(order.id) },
                            onReorder = { onReorder(order.id) },
                        )
                    }
                    loading -> Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RangoColors.Primary)
                    }
                    else -> EmptyOrders(title = tab.emptyTitle, onExplore = onExplore)
                }
            }
        }
    }
}

@Composable
private fun OrdersHeader(selected: OrdersTab, onSelect: (OrdersTab) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Meus pedidos",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                OrdersTab.entries.forEach { tab ->
                    val active = tab == selected
                    Column(
                        // Largura do texto, para o sublinhado acompanhar o rótulo.
                        modifier = Modifier.width(IntrinsicSize.Max).clickable { onSelect(tab) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            tab.label,
                            color = if (active) RangoColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.5.sp,
                            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(if (active) RangoColors.Primary else Color.Transparent, RoundedCornerShape(2.dp)),
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun OrderCard(order: OrderSummary, onOpen: () -> Unit, onTrack: () -> Unit, onReorder: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    order.code,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
                Text(order.date, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
            StatusChip(order.status, order.statusLabel)
        }
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onOpen),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Box(Modifier.size(46.dp).background(Color.hsv(order.hue, 0.3f, 0.97f), RoundedCornerShape(14.dp)))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    order.itemsSummary,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${itemsLabel(order.itemCount)} · ${formatMoney(order.total)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Icon(RangoIcons.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            val buttonShape = RoundedCornerShape(14.dp)
            if (order.status == OrderStatus.Live) {
                Button(
                    onClick = onTrack,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text("Acompanhar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            OutlinedButton(
                onClick = onReorder,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = buttonShape,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            ) {
                Text("Pedir novamente", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Etiqueta colorida do status: laranja em andamento, verde entregue, vermelho cancelado.
@Composable
internal fun StatusChip(status: OrderStatus, label: String) {
    val color = when (status) {
        OrderStatus.Live -> RangoColors.Primary
        OrderStatus.Done -> RangoColors.Success
        OrderStatus.Cancelled -> RangoColors.Error
    }
    Text(
        label,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(9.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@Composable
private fun EmptyOrders(title: String, onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        val borderColor = MaterialTheme.colorScheme.outline
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(32.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(42.dp))
        }
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.5.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Quando você fizer um pedido, ele aparece aqui.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 240.dp),
        )
        Button(
            onClick = onExplore,
            modifier = Modifier.padding(top = 4.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Fazer um pedido", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
private fun OrdersScreenPreview() {
    RangoTheme(darkTheme = false) {
        OrdersScreen(
            liveOrders = SampleOrderHistory.filter { it.status == OrderStatus.Live },
            pastOrders = SampleOrderHistory.filter { it.status != OrderStatus.Live },
            loading = false,
            onOpenOrder = {},
            onTrackOrder = {},
            onReorder = {},
            onExplore = {},
        )
    }
}

@Preview
@Composable
private fun OrdersScreenEmptyDarkPreview() {
    RangoTheme(darkTheme = true) {
        OrdersScreen(
            liveOrders = emptyList(),
            pastOrders = emptyList(),
            loading = false,
            onOpenOrder = {},
            onTrackOrder = {},
            onReorder = {},
            onExplore = {},
        )
    }
}
